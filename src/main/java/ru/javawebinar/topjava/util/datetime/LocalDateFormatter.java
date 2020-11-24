package ru.javawebinar.topjava.util.datetime;

import org.springframework.format.Formatter;
import ru.javawebinar.topjava.util.DateTimeUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateFormatter implements Formatter<LocalDate> {
    private String pattern;

    public LocalDateFormatter(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public LocalDate parse(String text, Locale locale) {
        return DateTimeUtil.parseLocalDate(text);
    }

    @Override
    public String print(LocalDate object, Locale locale) {
        return object == null ? "" : DateTimeFormatter.ofPattern(pattern, locale).format(object);
    }
}
