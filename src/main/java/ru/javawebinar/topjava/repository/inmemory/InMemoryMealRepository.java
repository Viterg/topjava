package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);
    private final Map<Integer, UserInMemoryMealRepository> commonRepository = new ConcurrentHashMap<>();

    public InMemoryMealRepository() {
        UserInMemoryMealRepository repo1 = new UserInMemoryMealRepository();
        MealsUtil.meals.forEach(repo1::save);
        commonRepository.put(1, repo1);
        UserInMemoryMealRepository repo2 = new UserInMemoryMealRepository();
        Stream.of(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                  new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000)).forEach(repo2::save);
        commonRepository.put(2, repo2);
    }

    @Override
    public Meal save(int userId, Meal meal) {
        log.info("save for user {}", userId);
        initIfNoContains(userId);
        return commonRepository.get(userId).save(meal);
    }

    @Override
    public boolean delete(int userId, int id) {
        log.info("delete for user {}", userId);
        initIfNoContains(userId);
        return commonRepository.get(userId).delete(id);
    }

    @Override
    public Meal get(int userId, int id) {
        log.info("get for user {}", userId);
        initIfNoContains(userId);
        return commonRepository.get(userId).get(id);
    }

    @Override
    public Collection<Meal> getAllForUser(int userId) {
        log.info("getAll for user {}", userId);
        initIfNoContains(userId);
        return commonRepository.get(userId).getAll();
    }

    private void initIfNoContains(int userId) {
        if (!commonRepository.containsKey(userId)) {
            commonRepository.put(userId, new UserInMemoryMealRepository());
        }
    }
}