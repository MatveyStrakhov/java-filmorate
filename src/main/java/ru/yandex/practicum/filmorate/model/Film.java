package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.ReleaseDateConstraint;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private int id;
    @NotBlank(message = "name mustn't be blank or null")
    private String name;
    @NotNull(message = "description mustn't be null")
    @Size(max = 200,message = "description is too long")
    private String description;
    @NotNull(message = "release date mustn't be null")
    @ReleaseDateConstraint
    private LocalDate releaseDate;
    @Positive(message = "duration is negative")
    private int duration;
    @JsonIgnore
    private final Set<Integer> likes = new HashSet<>();
    }
