package ru.yandex.practicum.filmorate.validator;


import ru.yandex.practicum.filmorate.annotation.LoginConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LoginValidator implements ConstraintValidator<LoginConstraint, String> {

    @Override
    public boolean isValid(String login,
                           ConstraintValidatorContext cxt) {
        return !login.contains(" ");
    }

}