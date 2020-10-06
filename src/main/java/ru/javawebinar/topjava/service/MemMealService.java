package ru.javawebinar.topjava.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MemMealService implements MealService {
    private static final Logger        log         = LoggerFactory.getLogger(MemMealService.class);
    private static final AtomicInteger commonIndex = new AtomicInteger(0);
    private static final List<Meal>    meals       = Collections.synchronizedList(new ArrayList<>());

    public MemMealService() {
        add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        add(new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }

    @Override
    public Collection<MealTo> getAll() {
        return MealsUtil.filteredByStreams(meals, LocalTime.MIN, LocalTime.MAX, 2000);
    }

    @Override
    public Meal getById(int mealId) {
        log.debug("Get meal with id " + mealId);
        return meals.get(mealId);
    }

    @Override
    public void add(Meal meal) {
        int id = commonIndex.get();
        meal.setId(id);
        log.debug("Add meal with id " + id);
        meals.add(meal);
        commonIndex.getAndIncrement();
    }

    @Override
    public void update(Meal meal) {
        int id = meal.getId();
        log.debug("Update meal with id " + id);
        meals.remove(id);
        meals.add(id, meal);
    }

    @Override
    public void delete(int mealId) {
        log.debug("Delete meal with id " + mealId);
        meals.removeIf(meal -> meal.getId() == mealId);
        commonIndex.getAndDecrement();
    }
}