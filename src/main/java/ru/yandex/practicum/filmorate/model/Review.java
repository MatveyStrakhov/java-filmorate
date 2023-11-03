package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class Review {
    private int reviewId;
    @Size(max = 255, message = "content is too long")
    @NotNull(message = "content mustn't be null")
    private String content;
    private Boolean isPositive;
    @NotNull(message = "userId mustn't be null")
    private Integer userId;
    @NotNull(message = "filmId mustn't be null")
    private Integer filmId;
    private int useful;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("reviewId", reviewId);
        values.put("content", content);
        values.put("isPositive", getIsPositive());
        values.put("userId", userId);
        values.put("filmId", filmId);
        values.put("useful", useful);
        return values;
    }

    @JsonGetter
    public boolean getIsPositive() {
        return isPositive;
    }
}
