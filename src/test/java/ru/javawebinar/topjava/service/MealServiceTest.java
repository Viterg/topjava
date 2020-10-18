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
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.Util;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.*;
import java.util.List;

import static org.junit.Assert.*;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;

@ContextConfiguration({
      "classpath:spring/spring-app.xml",
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
        Meal newMeal = getNew();
        Meal created = service.create(newMeal, USER_ID);
        Integer newId = created.getId();
        newMeal.setId(newId);
        assertEquals(created, newMeal);
        assertEquals(service.get(newId, USER_ID), newMeal);
    }

    @Test
    public void duplicateDatetime() throws Exception {
        assertThrows(DataAccessException.class, () -> service
                .create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин лёгкий", 200), USER_ID));
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, ADMIN_ID);
        assertEquals(service.get(100009, ADMIN_ID), updated);
    }

    @Test
    public void updateAnothers() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdated(), USER_ID));
    }

    @Test
    public void delete() {
        service.delete(100003, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(100003, USER_ID));
    }

    @Test
    public void deletedNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.delete(100010, USER_ID));
    }

    @Test
    public void getNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.get(100010, USER_ID));
    }

    @Test
    public void get() {
        assertEquals(service.get(100002, USER_ID), userMeals.get(0));
    }

    @Test
    public void getAll() {
        assertArrayEquals(service.getAll(USER_ID).toArray(new Meal[0]),
                          getByDateSortedMeals(userMeals).toArray(new Meal[0]));
    }

    @Test
    public void getBetweenInclusive() {
        LocalDate startDate = LocalDate.of(2020, Month.JANUARY, 31);
        LocalDate endDate = LocalDate.of(2020, Month.JANUARY, 31);
        List<Meal> betweenInclusive = service.getBetweenInclusive(startDate, endDate, USER_ID);

        LocalDateTime startDateTime = DateTimeUtil.atStartOfDayOrMin(startDate);
        LocalDateTime endDateTime = DateTimeUtil.atStartOfNextDayOrMax(endDate);
        List<Meal> filtredUserMeals = getFiltredUserMeals(userMeals, meal -> Util
                .isBetweenHalfOpen(meal.getDateTime(), startDateTime, endDateTime));

        assertArrayEquals(betweenInclusive.toArray(new Meal[0]), filtredUserMeals.toArray(new Meal[0]));
    }
}