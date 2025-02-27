package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.SinceDate;
import java.time.LocalDate;

public class SinceDayValidator implements ConstraintValidator<SinceDate, LocalDate> {
    private LocalDate referenceDate;

    @Override
    public void initialize(SinceDate constraintAnnotation) {
        referenceDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value != null && value.isBefore(referenceDate)) {
            context.disableDefaultConstraintViolation();
            String message = "должно быть не раньше " + referenceDate;
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        return true;
    }
}
