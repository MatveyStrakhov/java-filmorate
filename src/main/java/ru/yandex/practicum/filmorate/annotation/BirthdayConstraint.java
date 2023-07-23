package ru.yandex.practicum.filmorate.annotation;

import ru.yandex.practicum.filmorate.validator.BirthdayValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BirthdayValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface BirthdayConstraint {
    String message() default "Birthday is in future!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
