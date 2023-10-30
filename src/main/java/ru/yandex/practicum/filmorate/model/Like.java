package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Like {
    Integer userId;
    Integer filmId;
    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("user_id",userId);
        values.put("film_id",filmId);
        return values;
    }
}

