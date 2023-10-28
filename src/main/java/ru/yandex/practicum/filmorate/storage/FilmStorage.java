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

    List<Film> findPopularFilms(Integer count);

    // поиск популярных фильмов по году
    List<Film> findPopularFilms(Integer count, Integer year);

    // поиск популярных фильмов по жанру
    List<Film> findPopularFilms(Integer count, Long genreId);

    // поиск популярных фильмов по году и жанру
    List<Film> findPopularFilms(Integer count, Long genreId, Integer year);
}