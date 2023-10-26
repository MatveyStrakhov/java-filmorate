package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Director {
    private Integer directorId;
    private String directorName;

    public Director(Integer director_id, String director) {
        this.directorId = director_id;
        this.directorName = director;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("director_id", directorId);
        values.put("director", directorName);
        return values;

    }

}
