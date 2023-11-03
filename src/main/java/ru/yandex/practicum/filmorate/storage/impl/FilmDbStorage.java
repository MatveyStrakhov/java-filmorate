package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.FilmsExtractor;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
@Slf4j
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmsExtractor filmsExtractor;
    private final EventDao eventDao;

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("films").usingGeneratedKeyColumns("id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue());
        if (film.getGenres() != null) {
            film.setGenres(film.getGenres().stream().sorted(Comparator.comparing(Genre::getId, Integer::compareTo)).collect(Collectors.toCollection(LinkedHashSet::new)));
            String sql = "MERGE INTO film_genre VALUES (?, ?);";
            film.getGenres().forEach((Genre genre) -> jdbcTemplate.update(sql, film.getId(), genre.getId()));
        }
        if (film.getDirectors() != null) {
            String sql = "MERGE INTO film_director VALUES (?, ?);";
            film.getDirectors().forEach((Director director) -> jdbcTemplate.update(sql, film.getId(), director.getDirectorId()));
        }
        return film;
    }

    @Override
    public Collection<Film> returnAllFilms() {
        String sql = "SELECT * FROM films AS f " +
                "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_genre AS fg ON f.id = fg.film_id " +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_director AS fd ON f.id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id;";
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
                + "WHERE id = ?";
        log.info("film update started");
        String sqlForDeleteGenres = "DELETE FROM film_genre WHERE film_id = ?;";
        int numberOfDeletedGenres = jdbcTemplate.update(sqlForDeleteGenres, film.getId());
        log.info("Genres deleted: " + numberOfDeletedGenres);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genre VALUES(?,?);";
            film.getGenres().forEach((Genre genre) -> jdbcTemplate.update(sql, film.getId(), genre.getId()));
        }
        String sqlDeleteDirectors = "DELETE FROM film_director WHERE film_id = ?;";
        int numberOfDeletedDirectors = jdbcTemplate.update(sqlDeleteDirectors, film.getId());
        log.info("Directors deleted:" + numberOfDeletedDirectors);
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            String sql = "INSERT INTO film_director VALUES(?,?);";
            film.getDirectors().forEach((Director director) -> jdbcTemplate.update(sql, film.getId(), director.getDirectorId()));
        }
        jdbcTemplate.update(sqlQuery, film.getDescription(), film.getRating().getId(), film.getDuration(), film.getName(), film.getReleaseDate(), film.getId());
        return getFilmById(film.getId());
    }

    @Override
    public Film getFilmById(int filmId) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "f.rating_id, r.rating_name, fg.genre_id, g.genre, fd.director_id, d.director " +
                "FROM films AS f LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_genre AS fg ON f.id=fg.film_id " +
                "LEFT JOIN genre AS g ON fg.genre_id=g.genre_id " +
                "LEFT JOIN film_director AS fd ON f.id=fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id=d.director_id " +
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
    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "f.rating_id, r.rating_name, fg.genre_id, g.genre, fd.director_id, d.director, " +
                "COUNT(l.user_id) as count " +
                "FROM films AS f " + "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_genre AS fg ON f.id=fg.film_id " +
                "LEFT JOIN genre AS g ON fg.genre_id=g.genre_id " +
                "LEFT JOIN film_director AS fd ON f.id=fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id=d.director_id " +
                "WHERE d.director_id=";
        String sqlQuery;
        if (sortBy.equals("likes")) {
            sqlQuery = sql + directorId + "GROUP BY f.id, fg.genre_id ORDER BY count DESC;";
        } else if (sortBy.equals("year")) {
            sqlQuery = sql + directorId + "GROUP BY f.id, fg.genre_id ORDER BY f.release_date ASC;";
        } else {
            sqlQuery = sql + directorId + "GROUP BY f.id, fg.genre_id ";
        }
        return jdbcTemplate.query(sqlQuery, filmsExtractor);
    }

    @Override
    public void likeFilm(Integer filmId, Integer userId) {
        eventDao.eventAdd(filmId, "LIKE", "ADD", userId);
        String sql = "MERGE INTO likes(user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public void unlikeFilm(Integer filmId, Integer userId) {
        eventDao.eventAdd(filmId, "LIKE", "REMOVE", userId);
        String sql = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public boolean isValidFilm(int id) {
        return jdbcTemplate.queryForRowSet("SELECT id FROM films WHERE id=?", id).next();
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        String queryLower = query.toLowerCase();
        StringBuilder sqlBegin = new StringBuilder("SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "f.rating_id, r.rating_name, fg.genre_id, g.genre, fd.director_id, d.director, COUNT(l.USER_ID) AS count " +
                "FROM films AS f LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "LEFT JOIN film_genre AS fg ON f.id=fg.film_id " +
                "LEFT JOIN genre AS g ON fg.genre_id=g.genre_id " +
                "LEFT JOIN film_director AS fd ON f.id=fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id=d.director_id ");
        String sqlEnd = "GROUP BY F.ID " + "ORDER BY COUNT DESC;";
        switch (by) {
            case "title":
                StringBuilder sqlByTitle = sqlBegin.append("WHERE LOWER(f.name) LIKE '%")
                        .append(queryLower).append("%' ").append(sqlEnd);
                return jdbcTemplate.query(sqlByTitle.toString(), filmsExtractor);
            case "director":
                StringBuilder sqlByDirector = sqlBegin.append("WHERE LOWER(d.director) LIKE '%")
                        .append(queryLower).append("%' ").append(sqlEnd);
                return jdbcTemplate.query(sqlByDirector.toString(), filmsExtractor);
            case "director,title":
            case "title,director":
                StringBuilder sqlByTitleAndDirector = sqlBegin.append("WHERE LOWER(d.director) LIKE '%")
                        .append(queryLower).append("%' ").append("OR LOWER(f.name) LIKE '%").append(queryLower)
                        .append("%' ").append(sqlEnd);
                return jdbcTemplate.query(sqlByTitleAndDirector.toString(), filmsExtractor);
            default:
                return new ArrayList<>();
        }
    }

    @Override
    public List<Film> findPopularFilmsFromYear(Integer count, Integer year) {
        String sql = "SELECT f.*, r.rating_name, fg.genre_id, g.genre, d.director, d.director_id " +
                "FROM likes AS l " +
                "RIGHT OUTER JOIN films AS f ON l.film_id = f.id " +
                "LEFT OUTER JOIN film_genre fg on f.id = fg.film_id " +
                "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_director AS fd ON f.id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE EXTRACT(year FROM f.release_date) = ? " +
                "GROUP BY f.id, r.rating_name, fg.genre_id, g.genre, d.director " +
                "ORDER BY COUNT(l.user_id) DESC LIMIT ?";
        return jdbcTemplate.query(sql, filmsExtractor, year, count);
    }

    @Override
    public List<Film> findPopularFilmsFromGenre(Integer count, Long genreId) {
        String sql = "SELECT f.*, r.rating_name, fg.genre_id, g.genre, d.director, d.director_id " +
                "FROM likes AS l " +
                "RIGHT OUTER JOIN films AS f ON f.id = l.film_id " +
                "LEFT OUTER JOIN film_genre fg ON f.id = fg.film_id " +
                "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_director AS fd ON f.ID = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE f.id IN (SELECT DISTINCT film_id FROM " +
                " (SELECT fg2.film_id , COUNT(l2.user_id) AS c " +
                " FROM likes AS l2 RIGHT OUTER JOIN film_genre AS fg2 ON fg2.film_id = l2.film_id " +
                " WHERE fg2.genre_id = ? " +
                " GROUP BY fg2.film_id " +
                " ORDER BY c DESC ) " +
                " LIMIT ?) " +
                "GROUP BY f.id, r.rating_name, fg.genre_id, g.genre " +
                "ORDER BY COUNT(l.user_id) DESC";
        return jdbcTemplate.query(sql, filmsExtractor, genreId, count);
    }

    @Override
    public List<Film> findPopularFilmsFromYearAndGenre(Integer count, Long genreId, Integer year) {
        Date startDate = (year != null) ? Date.valueOf(year + "-01-01") : null;
        String sql = "SELECT f.*, r.rating_name, fg.genre_id, g.genre, d.director, d.director_id " +
                "FROM likes AS l " +
                "RIGHT OUTER JOIN films AS f ON f.id = l.film_id " +
                "LEFT OUTER JOIN film_genre fg ON f.id = fg.film_id " +
                "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_director AS fd ON f.id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE f.id IN (SELECT DISTINCT film_id FROM " +
                " (SELECT fg2.film_id , COUNT(l2.user_id) AS c " +
                " FROM likes AS l2 RIGHT OUTER JOIN film_genre AS fg2 ON fg2.film_id = l2.film_id " +
                " WHERE fg2.genre_id = ? " +
                " GROUP BY fg2.film_id " +
                " ORDER BY c DESC ) " +
                " LIMIT ?) " +
                "AND (f.release_date >= ? OR f.release_date IS NULL) " +
                "GROUP BY f.id, r.rating_name, fg.genre_id, g.genre " +
                "ORDER BY COUNT(l.user_id) DESC";
        return jdbcTemplate.query(sql, filmsExtractor, genreId, count, startDate);
    }

    @Override
    public List<Film> findPopularFilmsFromLikes(Integer count) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "f.rating_id, r.rating_name, fg.genre_id, g.genre, fd.director_id, d.director, " +
                "COUNT(l.user_id) AS count " +
                "FROM films AS f " +
                "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_genre AS fg ON f.id = fg.film_id " +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_director AS fd ON f.id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE f.id IN (SELECT DISTINCT id FROM " +
                "(SELECT id , COUNT(l2.user_id) AS c " +
                "FROM films AS f2 " +
                "LEFT JOIN likes AS l2 ON f2.id = l2.film_id " +
                "GROUP BY id " +
                "ORDER BY c DESC ) " +
                "LIMIT ?) " +
                "GROUP BY f.id, fg.genre_id " +
                "ORDER BY count DESC;";
        return jdbcTemplate.query(sql, filmsExtractor, count);
    }

    public void deleteFilm(int filmId) {
        if (isValidFilm(filmId)) {
            String sqlForFilms = "DELETE FROM films WHERE id = ?";
            String sqlForGenre = "DELETE FROM film_genre WHERE film_id = ?";
            String sqlForLikes = "DELETE FROM likes WHERE film_id = ?";
            jdbcTemplate.update(sqlForLikes, filmId);
            jdbcTemplate.update(sqlForGenre, filmId);
            jdbcTemplate.update(sqlForFilms, filmId);
        } else throw new IdNotFoundException("Director not found!");
    }

    @Override
    public Collection<Film> getFilmsByUser(Integer userId) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "f.rating_id, r.rating_name, fg.genre_id, g.genre, fd.director_id, d.director, " +
                "FROM films AS f " +
                "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_genre AS fg ON f.id=fg.film_id " +
                "LEFT JOIN genre AS g ON fg.genre_id=g.genre_id " +
                "LEFT JOIN film_director AS fd ON f.id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE f.id IN (SELECT film_id FROM likes WHERE user_id = ?)";
        Collection<Film> films = jdbcTemplate.query(sql, filmsExtractor, userId);
        if (films != null && !films.isEmpty()) {
            return films;
        } else {
            return new ArrayList<>();
        }
    }
}