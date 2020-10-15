package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);
    private final Map<Integer, UserInMemoryMealRepository> commonRepository = new ConcurrentHashMap<>();

    public InMemoryMealRepository() {
        MealsUtil.meals.forEach(userId -> save(1, userId));
        Stream.of(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак второго", 500),
                  new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак второго", 1000))
              .forEach(meal -> save(2, meal));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        log.info("save for user {}", userId);
        return commonRepository.computeIfAbsent(userId, id -> new UserInMemoryMealRepository()).save(meal);
    }

    @Override
    public boolean delete(int userId, int id) {
        log.info("delete for user {}", userId);
        UserInMemoryMealRepository mealRepository = commonRepository.get(userId);
        return mealRepository != null && mealRepository.delete(id);
    }

    @Override
    public Meal get(int userId, int id) {
        log.info("get for user {}", userId);
        UserInMemoryMealRepository mealRepository = commonRepository.get(userId);
        return mealRepository == null ? null : mealRepository.get(id);
    }

    @Override
    public List<Meal> getAllForUser(int userId) {
        log.info("getAll for user {}", userId);
        UserInMemoryMealRepository mealRepository = commonRepository.get(userId);
        return mealRepository == null ? Collections.emptyList() : filterMealsByPredicate(mealRepository.getAll(),
                                                                                         meal -> true);
    }

    @Override
    public List<Meal> getAllForUserFilteredByDates(int userId, LocalDate startDate, LocalDate endDate) {
        log.info("getAllSorted for user {}", userId);
        UserInMemoryMealRepository mealRepository = commonRepository.get(userId);
        return mealRepository == null ? Collections.emptyList() : filterMealsByPredicate(mealRepository.getAll(),
                         meal -> meal.getDate().compareTo(startDate) >= 0 && meal.getDate().compareTo(endDate) <= 0);
    }

    private List<Meal> filterMealsByPredicate(Collection<Meal> meals, Predicate<Meal> filter) {
        return meals.stream()
                    .filter(filter)
                    .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                    .collect(Collectors.toList());
    }
}