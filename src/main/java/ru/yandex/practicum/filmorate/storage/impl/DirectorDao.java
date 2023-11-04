package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorMapper;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Component
public class DirectorDao implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorMapper directorMapper;

    public DirectorDao(JdbcTemplate jdbcTemplate, DirectorMapper directorMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.directorMapper = directorMapper;
    }

    @Override
    public Director createDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        director.setDirectorId(simpleJdbcInsert.executeAndReturnKey(director.toMap()).intValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        if (isValidDirector(director.getDirectorId())) {
            String sql = "UPDATE directors SET director=? WHERE director_id=?";
            jdbcTemplate.update(sql, director.getDirectorName(), director.getDirectorId());
            return director;
        } else throw new IdNotFoundException("Incorrect director id!");

    }

    @Override
    public List<Director> getAllDirectors() {
        return jdbcTemplate.query("SELECT * FROM directors", directorMapper);

    }

    @Override
    public Director getDirectorById(int directorId) {
        if (isValidDirector(directorId)) {
            String sql = "SELECT * FROM directors WHERE director_id=" + directorId;
            return jdbcTemplate.queryForObject(sql, directorMapper);
        } else throw new IdNotFoundException("Incorrect director id!");

    }

    @Override
    public void deleteDirector(int directorId) {
        if (isValidDirector(directorId)) {
            String sqlForDirectors = "DELETE FROM directors WHERE director_id=?";
            String sqlForFilmDirector = "DELETE FROM film_director WHERE director_id=?";
            jdbcTemplate.update(sqlForFilmDirector, directorId);
            jdbcTemplate.update(sqlForDirectors, directorId);
        } else throw new IdNotFoundException("Director not found!");
    }

    @Override
    public boolean isValidDirector(int directorId) {
        return jdbcTemplate.queryForRowSet("SELECT director_id FROM directors WHERE director_id=?", directorId).next();

    }
}
