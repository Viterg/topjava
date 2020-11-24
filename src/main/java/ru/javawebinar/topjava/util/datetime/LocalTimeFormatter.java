package ru.javawebinar.topjava.util.datetime;

import org.springframework.format.Formatter;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalTimeFormatter implements Formatter<LocalTime> {
    private final String pattern;

    public LocalTimeFormatter(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public LocalTime parse(String text, Locale locale) {
        return DateTimeUtil.parseLocalTime(text);
    }

    @Override
    public String print(LocalTime object, Locale locale) {
        return object == null ? "" : DateTimeFormatter.ofPattern(pattern, locale).format(object);
    }
}