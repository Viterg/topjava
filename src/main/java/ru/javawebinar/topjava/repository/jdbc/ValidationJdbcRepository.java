package ru.javawebinar.topjava.repository.jdbc;

import ru.javawebinar.topjava.model.AbstractBaseEntity;

import javax.validation.*;
import java.util.Set;

interface ValidationJdbcRepository {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    default void validateEntity(AbstractBaseEntity entity) {
        Set<ConstraintViolation<AbstractBaseEntity>> violations = validator.validate(entity);
        if (!violations.isEmpty()) throw new ConstraintViolationException(violations);
    }
}