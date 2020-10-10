package ru.javawebinar.topjava.web;

import java.util.concurrent.atomic.AtomicInteger;

import static ru.javawebinar.topjava.util.MealsUtil.DEFAULT_CALORIES_PER_DAY;

public class SecurityUtil {
    public static AtomicInteger userId = new AtomicInteger(1);

    public static int authUserId() {
        return userId.get();
    }

    public static void setAuthUserId(int userId) {
        SecurityUtil.userId.set(userId);
    }

    public static int authUserCaloriesPerDay() {
        return DEFAULT_CALORIES_PER_DAY;
    }
}