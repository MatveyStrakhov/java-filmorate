package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreMapper;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreDao implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreMapper genreMapper;

    public List<Genre> getAllGenres() {
        return jdbcTemplate.query("SELECT * FROM genre", genreMapper);
    }

    public Genre getGenreById(int genreId) {
        if (isValidGenre(genreId)) {
            String sql = "SELECT * FROM genre WHERE genre_id=" + genreId;
            return jdbcTemplate.queryForObject(sql, genreMapper);
        } else {
            throw new IdNotFoundException("Id not found!");
        }
    }

    private boolean isValidGenre(int genreId) {
        return jdbcTemplate.queryForRowSet("SELECT genre_id FROM genre WHERE genre_id=?", genreId).next();
    }
}
