package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Rating {
    private Integer id;
    private String name;

    public Rating(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("rating_id", id);
        values.put("rating_name", name);
        return values;

    }
}

