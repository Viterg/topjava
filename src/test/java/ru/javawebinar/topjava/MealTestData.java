package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final Meal users100002  = new Meal(START_SEQ + 2, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0),
                                                     "Завтрак", 500);
    public static final Meal users100003  = new Meal(START_SEQ + 3, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0),
                                                     "Обед", 1000);
    public static final Meal users100004  = new Meal(START_SEQ + 4, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0),
                                                     "Ужин", 500);
    public static final Meal users100005  = new Meal(START_SEQ + 5, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0),
                                                     "Еда на граничное значение", 100);
    public static final Meal users100006  = new Meal(START_SEQ + 6, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0),
                                                     "Завтрак", 1000);
    public static final Meal users100007  = new Meal(START_SEQ + 7, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0),
                                                     "Обед", 500);
    public static final Meal users100008  = new Meal(START_SEQ + 8, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0),
                                                     "Ужин", 410);
    public static final Meal admins100009 = new Meal(START_SEQ + 9, LocalDateTime.of(2015, Month.JUNE, 1, 14, 0),
                                                     "Админ ланч", 510);
    public static final Meal admins100010 = new Meal(START_SEQ + 10, LocalDateTime.of(2015, Month.JUNE, 1, 21, 0),
                                                     "Админ ужин", 1500);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2020, 6, 28, 18, 0), "Новый ужин", 1555);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(admins100009);
        updated.setDescription("Админ новое");
        updated.setCalories(800);
        updated.setDateTime(LocalDateTime.of(2020, 6, 28, 18, 0));
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }
}