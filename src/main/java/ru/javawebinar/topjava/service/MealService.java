package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealService {
    List<Meal> getAll();

    void delete(int mealId);

    Meal getById(int mealId);

    Meal add(Meal meal);

    Meal update(Meal meal);
}