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
import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

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
        Meal created = service.create(getNew(), USER_ID);
        Meal newMeal = getNew();
        Integer newId = created.getId();
        newMeal.setId(newId);
        assertThat(created).usingRecursiveComparison().isEqualTo(newMeal);
        assertThat(service.get(newId, USER_ID)).usingRecursiveComparison().isEqualTo(newMeal);
    }

    @Test
    public void duplicateDatetime() throws Exception {
        assertThrows(DataAccessException.class, () -> service
                .create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин лёгкий", 200), USER_ID));
    }

    @Test
    public void update() {
        service.update(getUpdated(), ADMIN_ID);
        assertThat(service.get(START_SEQ + 9, ADMIN_ID)).usingRecursiveComparison().isEqualTo(getUpdated());
    }

    @Test
    public void updateAnothers() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdated(), USER_ID));
    }

    @Test
    public void updateNonExist() {
        assertThrows(NotFoundException.class, () -> {
            Meal newMeal = getNew();
            newMeal.setId(10);
            service.update(newMeal, USER_ID);
        });
    }

    @Test
    public void deletedNonExist() throws Exception {
        assertThrows(NotFoundException.class, () -> service.delete(10, USER_ID));
    }

    @Test
    public void getNonExist() throws Exception {
        assertThrows(NotFoundException.class, () -> service.get(10, USER_ID));
    }

    @Test
    public void delete() {
        service.delete(START_SEQ + 3, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(START_SEQ + 3, USER_ID));
    }

    @Test
    public void deletedNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.delete(START_SEQ + 10, USER_ID));
    }

    @Test
    public void getNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.get(START_SEQ + 10, USER_ID));
    }

    @Test
    public void get() {
        assertThat(service.get(START_SEQ + 2, USER_ID)).usingRecursiveComparison().isEqualTo(userMeals.get(0));
    }

    @Test
    public void getAll() {
        assertThat(service.getAll(USER_ID).toArray(new Meal[0])).usingRecursiveFieldByFieldElementComparator()
                                                                .isEqualTo(new Meal[]{dinner311, lunch311, breakfast311,
                                                                                      midnight, dinner301, lunch301,
                                                                                      breakfast301});
    }

    @Test
    public void getBetweenInclusive() {
        LocalDate startDate = LocalDate.of(2020, Month.JANUARY, 31);
        LocalDate endDate = LocalDate.of(2020, Month.JANUARY, 31);
        List<Meal> betweenInclusive = service.getBetweenInclusive(startDate, endDate, USER_ID);
        assertThat(betweenInclusive.toArray(new Meal[0])).usingRecursiveFieldByFieldElementComparator().isEqualTo(
                new Meal[]{dinner311, lunch311, breakfast311, midnight});
    }

    @Test
    public void getBetweenInclusiveByNull() {
        List<Meal> betweenInclusive = service.getBetweenInclusive(null, null, USER_ID);
        assertThat(betweenInclusive.toArray(new Meal[0])).usingRecursiveFieldByFieldElementComparator().isEqualTo(
                new Meal[]{dinner311, lunch311, breakfast311, midnight, dinner301, lunch301, breakfast301});
    }
}