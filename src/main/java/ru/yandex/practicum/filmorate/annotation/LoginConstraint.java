package ru.yandex.practicum.filmorate.annotation;


import ru.yandex.practicum.filmorate.validator.LoginValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LoginValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginConstraint {
    String message() default "Login contains blanks";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}