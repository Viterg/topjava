package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.util.Collection;

public interface MealService {

    Collection<MealTo> getAll();

    void delete(int mealId);

    Meal getById(int mealId);

    void add(Meal meal);

    void update(Meal meal);

}