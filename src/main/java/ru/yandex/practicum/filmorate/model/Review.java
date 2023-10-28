package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Review {
    private int reviewId;
    @Size(max = 255, message = "content is too long")
    @NotNull(message = "content mustn't be null")
    private String content;
    @NotNull(message = "positive mustn't be null")
    private boolean isPositive;
    @NotNull(message = "userId mustn't be null")
    private int userId;
    @NotNull(message = "filmId mustn't be null")
    private int filmId;
    private int useful;

    public Review( int reviewId,
                   @JsonProperty(value = "content" , required = true) String content,
                   @JsonProperty(value = "isPositive" , required = true) boolean isPositive,
                   @JsonProperty(value = "userId" , required = true) int userId,
                   @JsonProperty(value = "filmId" , required = true) int filmId, int useful) {
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

    @JsonGetter
    public boolean getIsPositive() {
        return isPositive;
    }
}
