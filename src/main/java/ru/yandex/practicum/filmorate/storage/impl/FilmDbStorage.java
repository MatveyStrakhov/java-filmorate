package ru.yandex.practicum.filmorate.storage.impl;

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
        switch (sortBy) {
            case "likes":
                sqlQuery = sql + directorId + "GROUP BY f.id, fg.genre_id ORDER BY count DESC;";
                break;
            case "year":
                sqlQuery = sql + directorId + "GROUP BY f.id, fg.genre_id ORDER BY f.release_date ASC;";
                break;
            default:
                sqlQuery = sql + directorId + "GROUP BY f.id, fg.genre_id ";
                break;
        }
        return jdbcTemplate.query(sqlQuery, filmsExtractor);
    }

    @Override
    public void likeFilm(Integer filmId, Integer userId) {
        EventDao.eventAdd(filmId, "LIKE", "ADD", userId);
        String sql = "MERGE INTO likes(user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public void unlikeFilm(Integer filmId, Integer userId) {
        EventDao.eventAdd(filmId, "LIKE", "REMOVE", userId);
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
        String sqlBegin = "SELECT f.id, f.name, f.description, f.release_date, f.duration,\n" +
                "f.rating_id, r.rating_name, fg.genre_id, g.genre, fd.director_id, d.director, COUNT(l.USER_ID) AS count \n" +
                "FROM films AS f LEFT JOIN rating AS r ON f.rating_id = r.rating_id\n" +
                "LEFT JOIN likes AS l ON f.id = l.film_id\n" +
                "LEFT JOIN film_genre AS fg ON f.id=fg.film_id\n" +
                "LEFT JOIN genre AS g ON fg.genre_id=g.genre_id\n" +
                "LEFT JOIN film_director AS fd ON f.id=fd.film_id\n" +
                "LEFT JOIN directors AS d ON fd.director_id=d.director_id\n";
        String sqlEnd = "GROUP BY F.ID\n" + "ORDER BY COUNT DESC;";
        String sqlByTitle = sqlBegin + "WHERE LOWER(f.NAME) LIKE '%" + queryLower + "%'\n" + sqlEnd;
        String sqlByDirector = sqlBegin + "WHERE LOWER(d.DIRECTOR) LIKE '%" + queryLower + "%'\n" + sqlEnd;
        String sqlByTitleAndDirector = sqlBegin +
                "WHERE LOWER(d.DIRECTOR) LIKE '%" + queryLower + "%'\n" +
                "OR LOWER(f.NAME) LIKE '%" + queryLower + "%'\n" + sqlEnd;
        switch (by) {
            case "title":
                return jdbcTemplate.query(sqlByTitle, filmsExtractor);
            case "director":
                return jdbcTemplate.query(sqlByDirector, filmsExtractor);
            case "director,title":
            case "title,director":
                return jdbcTemplate.query(sqlByTitleAndDirector, filmsExtractor);
            default:
                return new ArrayList<>();
        }
    }

    @Override
    public List<Film> findPopularFilms(Integer count, Integer year) {
        String sql = "SELECT f.*, r.rating_name, fg.genre_id, g.genre, d.director, d.director_id " +
                "FROM LIKES AS l " +
                "RIGHT OUTER JOIN FILMS AS f ON l.FILM_ID = f.ID " +
                "LEFT OUTER JOIN FILM_GENRE fg on f.ID = FG.FILM_ID " +
                "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_director AS fd ON f.ID = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE EXTRACT(YEAR FROM f.RELEASE_DATE) = ? " +
                "GROUP BY f.ID, r.rating_name, fg.genre_id, g.genre, d.director " +
                "ORDER BY COUNT(l.USER_ID) DESC LIMIT ?";
        return jdbcTemplate.query(sql, filmsExtractor, year, count);
    }

    @Override
    public List<Film> findPopularFilms(Integer count, Long genreId) {
        String sql = "SELECT f.*, r.rating_name, fg.genre_id, g.genre, d.director, d.director_id " +
                "FROM LIKES AS l " +
                "RIGHT OUTER JOIN FILMS AS f ON f.ID = l.FILM_ID " +
                "LEFT OUTER JOIN FILM_GENRE fg ON f.ID = FG.FILM_ID " +
                "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_director AS fd ON f.ID = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE f.ID IN (SELECT DISTINCT FILM_ID FROM " +
                " (SELECT fg2.FILM_ID , COUNT(l2.USER_ID) AS c " +
                " FROM LIKES AS l2 RIGHT OUTER JOIN FILM_GENRE AS fg2 ON fg2.FILM_ID = l2.FILM_ID " +
                " WHERE fg2.GENRE_ID = ? " +
                " GROUP BY fg2.FILM_ID " +
                " ORDER BY c DESC ) " +
                " LIMIT ?) " +
                "GROUP BY f.ID, r.rating_name, fg.genre_id, g.genre " +
                "ORDER BY COUNT(l.USER_ID) DESC";
        return jdbcTemplate.query(sql, filmsExtractor, genreId, count);
    }

    @Override
    public List<Film> findPopularFilms(Integer count, Long genreId, Integer year) {
        Date startDate = (year != null) ? Date.valueOf(year + "-01-01") : null;
        String sql = "SELECT f.*, r.rating_name, fg.genre_id, g.genre, d.director, d.director_id " +
                "FROM LIKES AS l " +
                "RIGHT OUTER JOIN FILMS AS f ON f.ID = l.FILM_ID " +
                "LEFT OUTER JOIN FILM_GENRE fg ON f.ID = FG.FILM_ID " +
                "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_director AS fd ON f.ID = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE f.ID IN (SELECT DISTINCT FILM_ID FROM " +
                " (SELECT fg2.FILM_ID , COUNT(l2.USER_ID) AS c " +
                " FROM LIKES AS l2 RIGHT OUTER JOIN FILM_GENRE AS fg2 ON fg2.FILM_ID = l2.FILM_ID " +
                " WHERE fg2.GENRE_ID = ? " +
                " GROUP BY fg2.FILM_ID " +
                " ORDER BY c DESC ) " +
                " LIMIT ?) " +
                "AND (f.release_date >= ? OR f.release_date IS NULL) " +
                "GROUP BY f.ID, r.rating_name, fg.genre_id, g.genre " +
                "ORDER BY COUNT(l.USER_ID) DESC";
        return jdbcTemplate.query(sql, filmsExtractor, genreId, count, startDate);
    }

    @Override
    public List<Film> findPopularFilms(Integer count) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "f.rating_id, r.rating_name, fg.genre_id, g.genre, fd.director_id, d.director, " +
                "COUNT(l.user_id) as count " +
                "FROM films AS f " +
                "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "LEFT JOIN rating AS r ON f.rating_id = r.rating_id " +
                "LEFT JOIN film_genre AS fg ON f.id = fg.film_id " +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN film_director AS fd ON f.id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE f.id IN (SELECT DISTINCT id FROM " +
                "(SELECT id , COUNT(l2.USER_ID) AS c " +
                "FROM films as f2 " +
                "LEFT JOIN likes AS l2 ON f2.ID = l2.FILM_ID " +
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
    public Collection<Film>  getFilmsByUser(Integer userId) {
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
            log.info("фильмы пользователя " + userId + ": " + films);
            return films;
        } else {
            return new ArrayList<>();
        }
    }
}