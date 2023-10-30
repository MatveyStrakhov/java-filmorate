package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RatingDao {
    private final JdbcTemplate jdbcTemplate;
    private final RatingMapper ratingMapper;

    public List<Rating> getAllRatings() {
        return jdbcTemplate.query("SELECT * FROM rating", ratingMapper);
    }

    public Rating getRatingById(int ratingId) {
        if (isValidRating(ratingId)) {
            String sql = "SELECT * FROM rating WHERE rating_id=" + ratingId;
            return jdbcTemplate.queryForObject(sql, ratingMapper);
        } else {
            throw new IdNotFoundException("Id not found!");
        }
    }

    private boolean isValidRating(int ratingId) {
        return jdbcTemplate.queryForRowSet("SELECT rating_id FROM rating WHERE rating_id=?", ratingId).next();
    }
}
