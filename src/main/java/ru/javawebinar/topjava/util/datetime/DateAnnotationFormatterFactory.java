package ru.javawebinar.topjava.util.datetime;

import org.springframework.format.*;

import java.time.LocalDate;
import java.util.Set;

public class DateAnnotationFormatterFactory implements AnnotationFormatterFactory<DateFormat> {
    @Override
    public Set<Class<?>> getFieldTypes() {
        return Set.of(LocalDate.class);
    }

    @Override
    public Printer<LocalDate> getPrinter(DateFormat annotation, Class<?> fieldType) {
        return configureFormatterFrom(annotation, fieldType);
    }

    @Override
    public Parser<LocalDate> getParser(DateFormat annotation, Class<?> fieldType) {
        return configureFormatterFrom(annotation, fieldType);
    }

    private Formatter<LocalDate> configureFormatterFrom(DateFormat annotation, Class<?> fieldType) {
        return new LocalDateFormatter(annotation.pattern().isEmpty() ? "yyyy-MM-dd" : annotation.pattern());
    }
}