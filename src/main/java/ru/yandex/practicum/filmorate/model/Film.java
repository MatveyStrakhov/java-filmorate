package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    int id;
    @NotNull
    @NotBlank
    String name;
    @NotNull
    String description;
    @NotNull
    LocalDate releaseDate;
    @Positive
    Duration duration;
    }
