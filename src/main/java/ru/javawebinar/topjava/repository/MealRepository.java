package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.util.List;

public interface MealRepository {
    // null if not found, when updated
    Meal save(int userId, Meal meal);

    // null if not found
    Meal get(int userId, int id);

    List<Meal> getAllForUser(int userId);

    List<Meal> getAllForUserFilteredByDates(int userId, LocalDate startDate, LocalDate endDate);

    // false if not found
    boolean delete(int userId, int id);
}