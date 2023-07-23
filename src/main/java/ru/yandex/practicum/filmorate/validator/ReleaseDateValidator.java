package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.annotation.ReleaseDateConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateConstraint, LocalDate> {

    @Override
    public boolean isValid(LocalDate releaseDate,
                           ConstraintValidatorContext cxt) {
        return releaseDate.isAfter(LocalDate.of(1895, 12, 28));
    }

}
