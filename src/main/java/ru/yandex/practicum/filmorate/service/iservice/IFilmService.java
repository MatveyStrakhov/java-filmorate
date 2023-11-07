package ru.yandex.practicum.filmorate.service.iservice;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface IFilmService {

    void likeFilm(int filmId, int userId);

    boolean isValidFilm(int id);

    void unlikeFilm(int filmId, int userId);

    List<Film> getFilmsByDirector(int directorId, String sortBy);

    Film createFilm(Film film);

    Collection<Film> returnAllFilms();

    Film updateFilm(Film film);

    Film getFilmById(int filmId);

    List<Film> searchFilms(String query, String by);

    List<Film> findPopularFilms(Integer count, Long genreId, Integer year);

    List<Film> findPopularFilmsFromLikes(Integer count);

    List<Film> findPopularFilmsFromYear(Integer count, Integer year);

    List<Film> findPopularFilmsFromGenre(Integer count, Long genreId);

    List<Film> findPopularFilmsFromYearAndGenre(Integer count, Long genreId, Integer year);

    void deleteFilm(int filmId);

    Collection<Film> getFilmsByUser(Integer userId);

    List<Film> getCommonFilms(Integer userId, Integer friendId);
}
