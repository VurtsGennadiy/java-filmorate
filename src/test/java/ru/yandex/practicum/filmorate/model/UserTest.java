package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {

    private final Validator validator;
    private Set<ConstraintViolation<User>> violations;
    User user;

    UserTest() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    void setUp() {
        violations = new HashSet<>();
        user = new User();
        user.setEmail("practicum@yandex.ru");
        user.setLogin("student");
        user.setName("Имя Фамилия");
        user.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void invalidEmailValidation() {
        user.setEmail("practicum.yandex.ru");
        violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    void nullLoginValidation() {
        user.setLogin(null);
        violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    void blankLoginValidation() {
        user.setLogin(" ");
        violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    void birthdayAfterTodayValidation() {
        user.setBirthday(LocalDate.now().plusDays(1));
        violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    void userValidation() {
        violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }
}
