package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Payload;
import jakarta.validation.Constraint;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import ru.yandex.practicum.filmorate.validator.SinceDayValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SinceDayValidator.class)
public @interface SinceDate {
    String value();

    String message() default "{SinceDate.invalid}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
