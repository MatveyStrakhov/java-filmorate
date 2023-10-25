package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Review {
    private int reviewId;
    @Size(max = 255, message = "content is too long")
    private String content;
    private boolean isPositive;
    private int userId;
    private int filmId;
    private int useful;

    public Review(int reviewId, String content, boolean isPositive, int userId, int filmId, int useful) {
        this.reviewId = reviewId;
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
        this.useful = useful;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("reviewId", reviewId);
        values.put("content", content);
        values.put("isPositive", isPositive);
        values.put("userId", userId);
        values.put("filmId", filmId);
        values.put("useful", useful);
        return values;
    }

}
