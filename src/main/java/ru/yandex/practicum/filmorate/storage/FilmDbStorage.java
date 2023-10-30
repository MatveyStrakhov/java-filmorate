package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmsExtractor filmsExtractor;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmsExtractor filmsExtractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmsExtractor = filmsExtractor;
    }

    @Override
    public Film createFilm(Film film) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("films").usingGeneratedKeyColumns("id");


        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue());
        if (film.getGenres() != null) {
            String sql = "merge into film_genre values(?,?);";
            film.getGenres().forEach((Genre genre) -> jdbcTemplate.update(sql, film.getId(), genre.getId()));
        }


        return film;
    }

    @Override
    public Collection<Film> returnAllFilms() {
        String sql = "SELECT * FROM films AS f " +
                "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_genre AS fg ON f.id=fg.film_id " +
                "LEFT JOIN genre AS g ON fg.genre_id=g.genre_id;";

        List<Film> films = jdbcTemplate.query(sql, filmsExtractor);
        if (films != null && !films.isEmpty()) {
            return films;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET " + "description = ?, rating_id = ?,duration = ?, name = ?, release_date = ? "
                + "where id = ?";
        log.info("film update started");
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sqlDelete = "DELETE FROM film_genre WHERE film_id = ?;";
            int numberOfDeleted = jdbcTemplate.update(sqlDelete, film.getId());
            log.info("Genres deleted:" + numberOfDeleted);
            String sql = "INSERT INTO film_genre VALUES(?,?);";
            film.getGenres().forEach((Genre genre) -> jdbcTemplate.update(sql, film.getId(), genre.getId()));
        } else {
            String sql = "DELETE FROM film_genre WHERE film_id = ?;";
            int numberOfDeleted = jdbcTemplate.update(sql, film.getId());
            log.info("Genres deleted: " + numberOfDeleted);
        }
        jdbcTemplate.update(sqlQuery, film.getDescription(), film.getRating().getId(), film.getDuration(), film.getName(), film.getReleaseDate(), film.getId());


        return getFilmById(film.getId());
    }

    @Override
    public Film getFilmById(int filmId) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "f.rating_id, r.rating_name, fg.genre_id, g.genre " +
                "FROM films AS f LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_genre AS fg ON f.id=fg.film_id " +
                "LEFT JOIN genre AS g ON fg.genre_id=g.genre_id " +
                "WHERE id = " + filmId + " ;";
        try {
            List<Film> films = jdbcTemplate.query(sqlQuery, filmsExtractor);
            if (films != null && !films.isEmpty()) {
                log.info("Film found: {} {}", films.get(0).getId(), films.get(0).getName());
                return films.get(0);
            } else {
                throw new IdNotFoundException("Film not found!");
            }
        } catch (DataAccessException e) {
            log.info("Data ACCESS: Film not found: " + filmId);
            throw new IdNotFoundException("Film not found:" + filmId);
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "f.rating_id, r.rating_name, fg.genre_id, g.genre, COUNT(l.user_id) as count " +
                "FROM films AS f " + "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_genre AS fg ON f.id=fg.film_id " +
                "LEFT JOIN genre AS g ON fg.genre_id=g.genre_id " +
                "GROUP BY f.id " +
                "ORDER BY count DESC " +
                "LIMIT " + count + ";";
        List<Film> films = jdbcTemplate.query(sql, filmsExtractor);
        if (films != null && !films.isEmpty()) {
            return films;
        } else {
            return new ArrayList<>();

        }
    }

    @Override
    public void likeFilm(Integer filmId, Integer userId) {
        String sql = "MERGE INTO likes(user_id, film_id) VALUES (?, ?)";
        log.info("пользователь " + userId + " лайкнул фильм " + filmId);
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public void unlikeFilm(Integer filmId, Integer userId) {
        String sql = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        log.info("пользователь " + userId + " дизлайкнул фильм " + filmId);
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public boolean isValidFilm(int id) {
        return jdbcTemplate.queryForRowSet("SELECT id FROM films WHERE id=?", id).next();
    }

    @Override
    public Collection<Film> getFilmsByUser(Integer userId) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "f.rating_id, r.rating_name, fg.genre_id, g.genre, COUNT(l.user_id) as count " +
                "FROM films AS f " + "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_genre AS fg ON f.id=fg.film_id " +
                "LEFT JOIN genre AS g ON fg.genre_id=g.genre_id " +
                "WHERE f.id IN (SELECT l.film_id FROM likes WHERE l.user_id = " + userId + ")";
        Collection<Film> films = jdbcTemplate.query(sql, filmsExtractor);
        if (films != null && !films.isEmpty()) {
            log.info("фильмы пользователя " + userId + ": " + films);
            return films;
        } else {
            return new ArrayList<>();
        }
    }

}
