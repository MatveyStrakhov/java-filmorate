package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Film createFilm(Film film);

    Collection<Film> returnAllFilms();

    Film updateFilm(Film film);

    Film getFilmById(int filmId);

    List<Film> getFilmsByDirector(int directorId, String sortBy);

    void likeFilm(Integer filmId, Integer userId);

    void unlikeFilm(Integer filmId, Integer userId);

    boolean isValidFilm(int id);

    List<Film> searchFilms(String query, String by);

    List<Film> findPopularFilmsFromLikes(Integer count);

    List<Film> findPopularFilmsFromYear(Integer count, Integer year);

    List<Film> findPopularFilmsFromGenre(Integer count, Long genreId);

    List<Film> findPopularFilmsFromYearAndGenre(Integer count, Long genreId, Integer year);

    void deleteFilm(int filmId);

    Collection<Film> getFilmsByUser(Integer userId);
}