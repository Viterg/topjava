package ru.javawebinar.topjava.service;

import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.*;
import java.util.List;

import static org.junit.Assert.*;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;

@ContextConfiguration({
      "classpath:spring/spring-app.xml",
      "classpath:spring/spring-repo-jdbc.xml",
      "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {
    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void create() {
        Meal created = service.create(getNew(), USER_ID);
        Meal newMeal = getNew();
        Integer newId = created.getId();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, USER_ID), newMeal);
    }

    @Test
    public void duplicateDatetime() throws Exception {
        assertThrows(DataAccessException.class,
                     () -> service.create(new Meal(users100004.getDateTime(), "Ужин лёгкий", 200), USER_ID));
    }

    @Test
    public void update() {
        service.update(getUpdated(), ADMIN_ID);
        assertMatch(service.get(admins100009.getId(), ADMIN_ID), getUpdated());
    }

    @Test
    public void updateAnothers() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdated(), USER_ID));
    }

    @Test
    public void updateNonExist() {
        assertThrows(NotFoundException.class, () -> {
            Meal newMeal = getNew();
            newMeal.setId(NON_EXIST_MEAL_ID);
            service.update(newMeal, USER_ID);
        });
    }

    @Test
    public void deletedNonExist() throws Exception {
        assertThrows(NotFoundException.class, () -> service.delete(NON_EXIST_MEAL_ID, USER_ID));
    }

    @Test
    public void getNonExist() throws Exception {
        assertThrows(NotFoundException.class, () -> service.get(NON_EXIST_MEAL_ID, USER_ID));
    }

    @Test
    public void delete() {
        service.delete(users100003.getId(), USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(users100003.getId(), USER_ID));
    }

    @Test
    public void deletedNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.delete(admins100010.getId(), USER_ID));
    }

    @Test
    public void getNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.get(admins100010.getId(), USER_ID));
    }

    @Test
    public void get() {
        assertMatch(service.get(users100002.getId(), USER_ID), users100002);
    }

    @Test
    public void getAll() {
        assertMatch(service.getAll(USER_ID), users100008, users100007, users100006, users100005, users100004,
                    users100003, users100002);
    }

    @Test
    public void getBetweenInclusive() {
        LocalDate startDate = LocalDate.of(2020, Month.JANUARY, 31);
        LocalDate endDate = LocalDate.of(2020, Month.JANUARY, 31);
        List<Meal> betweenInclusive = service.getBetweenInclusive(startDate, endDate, USER_ID);
        assertMatch(betweenInclusive, users100008, users100007, users100006, users100005);
    }

    @Test
    public void getBetweenInclusiveByNull() {
        List<Meal> betweenInclusive = service.getBetweenInclusive(null, null, USER_ID);
        assertMatch(betweenInclusive, users100008, users100007, users100006, users100005, users100004, users100003,
                    users100002);
    }
}