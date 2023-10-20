package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RatingMapper implements RowMapper<Rating> {
    @Override
    public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
        Rating rating = Rating.builder()
                .id(rs.getInt("rating_id"))
                .name(rs.getString("rating_name"))
                .build();
        return rating;
    }
}
