package ru.javawebinar.topjava.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MemMealService implements MealService {
    private static final Logger             log     = LoggerFactory.getLogger(MemMealService.class);
    private final        AtomicInteger      counter = new AtomicInteger(0);
    private final        Map<Integer, Meal> meals   = new ConcurrentHashMap<>();

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
    public List<Meal> getAll() {
        log.debug("Get all meals");
        return new ArrayList<>(meals.values());
    }

    @Override
    public Meal getById(int mealId) {
        log.debug("Get meal with id {}", mealId);
        return meals.get(mealId);
    }

    @Override
    public Meal add(Meal meal) {
        int id = counter.getAndIncrement();
        meal.setId(id);
        log.debug("Add meal with id {}", id);
        meals.put(id, meal);
        return meal;
    }

    @Override
    public Meal update(Meal meal) {
        int id = meal.getId();
        log.debug("Update meal with id {}", id);
        meals.replace(id, meal);
        return meal;
    }

    @Override
    public void delete(int mealId) {
        log.debug("Delete meal with id {}", mealId);
        meals.remove(mealId);
    }
}