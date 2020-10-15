package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.ValidationUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import static ru.javawebinar.topjava.web.SecurityUtil.*;

@Controller
public class MealRestController {
    private static final Logger      log = LoggerFactory.getLogger(MealRestController.class);
    private final        MealService service;

    @Autowired
    public MealRestController(MealService service) {
        this.service = service;
    }

    public List<MealTo> getAll() {
        log.info("getAll");
        return MealsUtil.getTos(service.getAllForUser(authUserId()), authUserCaloriesPerDay());
    }

    public List<MealTo> getAllFilteredByDates(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        log.info("get filtered meals");
        Collection<Meal> filteredByDates = service.getAllForUserFilteredByDates(authUserId(), startDate, endDate);
        return MealsUtil.getFilteredTos(filteredByDates, authUserCaloriesPerDay(), startTime, endTime);
    }

    public Meal create(Meal meal) {
        log.info("create {}", meal);
        ValidationUtil.checkNew(meal);
        return service.create(authUserId(), meal);
    }

    public void update(Meal meal, int id) {
        log.info("update {} with id={}", meal, id);
        ValidationUtil.assureIdConsistent(meal, id);
        service.update(authUserId(), meal);
    }

    public Meal get(int id) {
        log.info("get {}", id);
        return service.get(authUserId(), id);
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(authUserId(), id);
    }
}