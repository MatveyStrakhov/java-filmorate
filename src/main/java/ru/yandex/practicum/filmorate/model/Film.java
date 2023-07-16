package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    int id;
    @NotBlank(message = "name mustn't be blank or null")
    String name;
    @NotNull(message = "description mustn't be null")
    String description;
    @NotNull(message = "release date mustn't be null")
    LocalDate releaseDate;
    int duration;
    }
