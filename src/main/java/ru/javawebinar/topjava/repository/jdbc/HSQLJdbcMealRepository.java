package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
@Profile("hsqldb")
public class HSQLJdbcMealRepository extends AbstractJdbcMealRepository {

    @Autowired
    public HSQLJdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    public Meal save(Meal meal, int userId) {
        MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("id", meal.getId())
                .addValue("description", meal.getDescription())
                .addValue("calories", meal.getCalories())
                .addValue("date_time", DateTimeUtil.convertToOldFormat(meal.getDateTime()))
                .addValue("user_id", userId);
        return save(meal, userId, map);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        Date startDate = DateTimeUtil.convertToOldFormat(startDateTime);
        Date endDate = DateTimeUtil.convertToOldFormat(endDateTime);
        return jdbcTemplate.query(
                "SELECT * FROM meals WHERE user_id=?  AND date_time >=  ? AND date_time < ? ORDER BY date_time DESC",
                ROW_MAPPER, userId, startDate, endDate);
    }
}
