package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectorMapper implements RowMapper<Director> {
    @Override
    public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        Director director = Director.builder()
                .director_id(rs.getInt("director_id"))
                .director(rs.getString("director"))
                .build();
        return director;
    }
}
