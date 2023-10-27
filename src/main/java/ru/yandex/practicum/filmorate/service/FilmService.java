package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundExeption;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public void likeFilm(int filmId, int userId) {
        filmStorage.likeFilm(filmId, userId);
    }

    public boolean isValidFilm(int id) {
        return filmStorage.isValidFilm(id);
    }

    public void unlikeFilm(int filmId, int userId) {
        filmStorage.unlikeFilm(filmId, userId);
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Collection<Film> returnAllFilms() {
        return filmStorage.returnAllFilms();
    }

    public Film updateFilm(Film film) {
        if (isValidFilm(film.getId())) {
            return filmStorage.updateFilm(film);
        } else {
            throw new IdNotFoundException("This id doesn't exist!");
        }
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public void filmDeleteById(int filmId) { //метод удаления фильма по id
        if (filmStorage.getFilmById(filmId) == null) {
            throw new IncorrectIdException("Не ма такого фильма");
        }
        filmStorage.deleteFilmById(filmId);
    }

    //возвращает список первых фильмов по количеству лайков.
    public List<Film> findPopularFilms(Integer count) {
        if (count <= 0) {
            throw new NotFoundExeption("count");
        }

        if (filmStorage.findPopularFilms(count) != null) {
            return filmStorage.findPopularFilms(count);
        } else {
            return null;
        }
    }

    // поиск популярных фильмов по году
    public List<Film> findPopularFilms(Integer count, Integer year) {
        if (count <= 0) {
            throw new NotFoundExeption("count");
        }

        if (filmStorage.findPopularFilms(count, year) != null) {
            return filmStorage.findPopularFilms(count, year);
        } else {
            return null;
        }
    }

    // поиск популярных фильмов по жанру
    public List<Film> findPopularFilms(Integer count, Long genreId) {
        if (count <= 0) {
            throw new NotFoundExeption("count");

        }
        if (filmStorage.findPopularFilms(count, genreId) != null) {
            return filmStorage.findPopularFilms(count, genreId);
        } else {
            return null;
        }
    }

    // поиск популярных фильмов по году и жанру
    public List<Film> findPopularFilms(Integer count, Long genreId, Integer year) {
        if (count <= 0) {
            throw new NotFoundExeption("count");
        }
        if (filmStorage.findPopularFilms(count, genreId, year) != null) {
            return filmStorage.findPopularFilms(count, genreId, year);
        } else {
            return null;
        }
    }

}
