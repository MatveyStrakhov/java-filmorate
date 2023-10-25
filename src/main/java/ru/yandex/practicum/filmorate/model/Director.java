package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Director {
    private Integer director_id;
    private String director;

    public Director(Integer director_id, String director) {
        this.director_id = director_id;
        this.director = director;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("director_id", director_id);
        values.put("director", director);
        return values;

    }

}
