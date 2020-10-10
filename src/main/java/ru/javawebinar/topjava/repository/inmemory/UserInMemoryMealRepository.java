package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class UserInMemoryMealRepository {
    private static final Logger             log        = LoggerFactory.getLogger(UserInMemoryMealRepository.class);
    private final        Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final        AtomicInteger      counter    = new AtomicInteger(0);

    public Meal save(Meal meal) {
        log.info("save {}", meal);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }
        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    public boolean delete(int id) {
        log.info("delete {}", id);
        return repository.remove(id) != null;
    }

    public Meal get(int id) {
        log.info("get {}", id);
        return repository.get(id);
    }

    public Collection<Meal> getAll() {
        log.info("getAll");
        return repository.values().stream()
                         .sorted(Comparator.comparing(Meal::getDate).reversed())
                         .collect(Collectors.toList());
    }
}