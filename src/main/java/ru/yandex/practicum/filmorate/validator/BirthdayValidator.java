package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.annotation.BirthdayConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class BirthdayValidator implements ConstraintValidator<BirthdayConstraint, LocalDate> {
    @Override
    public void initialize(BirthdayConstraint birthdayConstraint) {
    }

    @Override
    public boolean isValid(LocalDate birthday,
                           ConstraintValidatorContext cxt) {
        return birthday.isBefore(LocalDate.now());
    }

}