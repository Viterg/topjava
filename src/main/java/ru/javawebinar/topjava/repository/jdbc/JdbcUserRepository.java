package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.List;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository, ValidationJdbcRepository {
    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate               jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert           insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        validateEntity(user);
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
            updateRoles("INSERT INTO user_roles (role, user_id) VALUES (?, ?)", user);
        } else {
            int updated = namedParameterJdbcTemplate.update("""
                               UPDATE users SET name=:name, email=:email, password=:password, 
                               registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                            """, parameterSource);
            if (updated == 0) return null;

            updateRoles("UPDATE user_roles SET role=? WHERE user_id = ?", user);
        }
        return user;
    }

    public void updateRoles(String query, User user) {
        Set<Role> roles = user.getRoles();
        jdbcTemplate.batchUpdate(query, roles, roles.size(),
                                 (ps, role) -> {
                                     ps.setString(1, role.name());
                                     ps.setInt(2, user.getId());
                                 });
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("""
                          SELECT u.*, string_agg(r.role, ',') AS roles FROM users u JOIN user_roles r ON u.id=r.user_id
                          WHERE id=? GROUP BY u.id""", ROW_MAPPER, id);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
        //        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("""
                          SELECT u.*, string_agg(r.role, ',') AS roles FROM users u JOIN user_roles r ON u.id=r.user_id
                           WHERE email=? GROUP BY u.id""", ROW_MAPPER, email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("""
                          SELECT u.*, string_agg(r.role, ',') AS roles FROM users u JOIN user_roles r ON u.id=r.user_id 
                          GROUP BY u.id, u.name, u.email ORDER BY u.name, u.email """, ROW_MAPPER);
    }
}