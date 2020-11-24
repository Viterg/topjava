package ru.javawebinar.topjava.util.datetime;

import org.springframework.format.*;

import java.time.LocalTime;
import java.util.Set;

public class TimeAnnotationFormatterFactory implements AnnotationFormatterFactory<TimeFormat> {
    @Override
    public Set<Class<?>> getFieldTypes() {
        return Set.of(LocalTime.class);
    }

    @Override
    public Printer<LocalTime> getPrinter(TimeFormat annotation, Class<?> fieldType) {
        return configureFormatterFrom(annotation, fieldType);
    }

    @Override
    public Parser<LocalTime> getParser(TimeFormat annotation, Class<?> fieldType) {
        return configureFormatterFrom(annotation, fieldType);
    }

    private Formatter<LocalTime> configureFormatterFrom(TimeFormat annotation, Class<?> fieldType) {
        return new LocalTimeFormatter(annotation.pattern().isEmpty() ? "HH:mm:ss" : annotation.pattern());
    }
}