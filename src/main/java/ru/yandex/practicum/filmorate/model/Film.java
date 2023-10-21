package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.ReleaseDateConstraint;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class Film {
    private int id;
    @NotBlank(message = "name mustn't be blank or null")
    private String name;
    @NotNull(message = "description mustn't be null")
    @Size(max = 200, message = "description is too long")
    private String description;
    @NotNull(message = "release date mustn't be null")
    @ReleaseDateConstraint
    private LocalDate releaseDate;
    @Positive(message = "duration is negative")
    private int duration;
    @NotNull
    @JsonAlias("mpa")
    private Rating mpa;
    private Set<Genre> genres;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("id", id);
        values.put("description", description);
        values.put("name", name);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("mpa", mpa);
        values.put("rating_id", mpa.getId());
        values.put("genres", genres);
        return values;
    }

    public Rating getRating() {
        return mpa;
    }
}
