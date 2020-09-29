package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        System.out.println();
        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime,
                                                            LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> dateAndCalories = collectDailyCalories(meals);
        List<UserMealWithExcess> filteredMeals = new ArrayList<>();
        for (UserMeal meal : meals) {
            LocalDateTime date = meal.getDateTime();
            if (TimeUtil.isBetweenHalfOpen(date.toLocalTime(), startTime, endTime)) {
                boolean excess = dateAndCalories.get(date.toLocalDate()) > caloriesPerDay;
                filteredMeals.add(new UserMealWithExcess(date, meal.getDescription(), meal.getCalories(), excess));
            }
        }
        return filteredMeals;
    }

    private static Map<LocalDate, Integer> collectDailyCalories(List<UserMeal> meals) {
        Map<LocalDate, Integer> dateAndCalories = new HashMap<>();
        for (UserMeal meal : meals) {
            dateAndCalories.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
        }
        return dateAndCalories;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime,
                                                             LocalTime endTime, int caloriesPerDay) {
        return meals.stream().collect(new UserMealsUtil.MealsCollector(caloriesPerDay, startTime, endTime));
    }

    private static class MealsCollector
            implements Collector<UserMeal, Map<LocalDate, List<UserMeal>>, List<UserMealWithExcess>> {

        private final int       caloriesPerDay;
        private final LocalTime startTime;
        private final LocalTime endTime;

        public MealsCollector(int caloriesPerDay, LocalTime startTime, LocalTime endTime) {
            this.caloriesPerDay = caloriesPerDay;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public Supplier<Map<LocalDate, List<UserMeal>>> supplier() {
            return HashMap::new;
        }

        @Override
        public BiConsumer<Map<LocalDate, List<UserMeal>>, UserMeal> accumulator() {
            return (map, meal) -> {
                LocalDate date = meal.getDateTime().toLocalDate();
                if (map.containsKey(date)) {
                    map.get(date).add(meal);
                } else {
                    List<UserMeal> meals = new ArrayList<>();
                    meals.add(meal);
                    map.put(date, meals);
                }
            };
        }

        @Override
        public BinaryOperator<Map<LocalDate, List<UserMeal>>> combiner() {
            return (m1, m2) -> {
                Map<LocalDate, List<UserMeal>> merged = Stream.of(m1, m2)
                                                              .map(Map::entrySet)
                                                              .flatMap(Set::stream)
                                                              .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                        Map.Entry::getValue, (a, b) -> {
                                                                          List<UserMeal> both = new ArrayList<>(a);
                                                                          both.addAll(b);
                                                                          return both;
                                                                      }));
                return merged;
            };
        }

        @Override
        public Function<Map<LocalDate, List<UserMeal>>, List<UserMealWithExcess>> finisher() {
            return m -> {
                List<UserMealWithExcess> result = new ArrayList<>();
                for (LocalDate k : m.keySet()) {
                    int dailyCalories = m.get(k).stream()
                                         .mapToInt(UserMeal::getCalories)
                                         .sum();
                    result.addAll(m.get(k).stream()
                                   .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                                   .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(),                                                                       meal.getCalories(),                                                                       dailyCalories > caloriesPerDay))
                                   .collect(Collectors.toList()));
                }
                return result;
            };
        }

        @Override
        public Set<Characteristics> characteristics() {
            return EnumSet.of(Characteristics.CONCURRENT, Characteristics.UNORDERED);
        }
    }
}