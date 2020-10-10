package ru.javawebinar.topjava.repository;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;

import java.util.Collection;

public interface MealRepository {
    // null if not found, when updated
    Meal save(int userId, Meal meal);

    // null if not found
    Meal get(int userId, int id);

    Collection<Meal> getAllForUser(int userId);

    // false if not found
    boolean delete(int userId, int id);
}