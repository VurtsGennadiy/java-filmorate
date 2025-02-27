package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.AfterCinemaDay;

import java.time.LocalDate;

public class AfterCinemaDayValidator implements ConstraintValidator<AfterCinemaDay, LocalDate> {
    public static final LocalDate CINEMA_DAY = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value != null) {
            return CINEMA_DAY.isBefore(value) || CINEMA_DAY.isEqual(value);
        }
        return false;
    }
}
