package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Film createFilm(Film film);

    Collection<Film> returnAllFilms();

    Film updateFilm(Film film);

    Film getFilmById(int filmId);

    List<Film> getPopularFilms(int count);

    void likeFilm(Integer filmId, Integer userId);

    void unlikeFilm(Integer filmId, Integer userId);

    boolean isValidFilm(int id);

    Collection<Film> getFilmsByUser(Integer userId);
}
