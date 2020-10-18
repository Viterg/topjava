package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MealTestData {
    public static final List<Meal> userMeals  = Arrays.asList(
            new Meal(100002, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
            new Meal(100003, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
            new Meal(100004, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
            new Meal(100005, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
            new Meal(100006, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
            new Meal(100007, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
            new Meal(100008, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    public static final List<Meal> adminMeals = Arrays.asList(
            new Meal(100009, LocalDateTime.of(2015, Month.JUNE, 1, 14, 0), "Админ ланч", 510),
            new Meal(100010, LocalDateTime.of(2015, Month.JUNE, 1, 21, 0), "Админ ужин", 1500));

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2020, 6, 28, 18, 0), "Новый ужин", 1555);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(adminMeals.get(0));
        updated.setDescription("Админ новое");
        updated.setCalories(800);
        return updated;
    }

    public static List<Meal> getByDateSortedMeals(List<Meal> meals) {
        return meals.stream()
                    .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                    .collect(Collectors.toList());
    }

    public static List<Meal> getFiltredUserMeals(List<Meal> meals, Predicate<? super Meal> filter) {
        return getByDateSortedMeals(meals.stream()
                                         .filter(filter)
                                         .collect(Collectors.toList()));
    }
}