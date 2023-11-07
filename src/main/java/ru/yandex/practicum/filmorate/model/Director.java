package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Director {

    @JsonAlias("id")
    @JsonProperty("id")
    private Integer directorId;
    @NotBlank
    @JsonProperty("name")
    @JsonAlias("name")
    private String directorName;

    public Director(Integer directorId, String director) {
        this.directorId = directorId;
        this.directorName = director;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("director_id", directorId);
        values.put("director", directorName);
        return values;
    }

}