package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
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

    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        return filmStorage.getFilmsByDirector(directorId, sortBy);
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

    public List<Film> searchFilms(String query, String by) {
        return filmStorage.searchFilms(query, by);
    }

    public List<Film> findPopularFilms(Integer count, Long genreId, Integer year) {
        if (genreId == null && year == null) {
            return findPopularFilmsFromLikes(count);
        } else if (genreId == null) {
            return findPopularFilmsFromYear(count, year);
        } else if (year == null) {
            return findPopularFilmsFromGenre(count, genreId);
        } else {
            return findPopularFilmsFromYearAndGenre(count, genreId, year);
        }
    }

    public List<Film> findPopularFilmsFromLikes(Integer count) {
        if (count <= 0) {
            throw new IdNotFoundException("count");
        }

        if (filmStorage.findPopularFilmsFromLikes(count) != null) {
            return filmStorage.findPopularFilmsFromLikes(count);
        } else {
            return null;
        }
    }

    public List<Film> findPopularFilmsFromYear(Integer count, Integer year) {
        if (count <= 0) {
            throw new IdNotFoundException("count");
        }

        if (filmStorage.findPopularFilmsFromYear(count, year) != null) {
            return filmStorage.findPopularFilmsFromYear(count, year);
        } else {
            return null;
        }
    }

    public List<Film> findPopularFilmsFromGenre(Integer count, Long genreId) {
        if (count <= 0) {
            throw new IdNotFoundException("count");

        }
        if (filmStorage.findPopularFilmsFromGenre(count, genreId) != null) {
            return filmStorage.findPopularFilmsFromGenre(count, genreId);
        } else {
            return null;
        }
    }

    public List<Film> findPopularFilmsFromYearAndGenre(Integer count, Long genreId, Integer year) {
        if (count <= 0) {
            throw new IdNotFoundException("count");
        }
        if (filmStorage.findPopularFilmsFromYearAndGenre(count, genreId, year) != null) {
            return filmStorage.findPopularFilmsFromYearAndGenre(count, genreId, year);
        } else {
            return null;
        }
    }

    public void deleteFilm(int filmId) {
        filmStorage.deleteFilm(filmId);
    }

    public Collection<Film> getFilmsByUser(Integer userId) {
        return filmStorage.getFilmsByUser(userId);
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        Collection<Film> listOfUserFilms = getFilmsByUser(userId);
        Collection<Film> listOfFriendFilms = getFilmsByUser(friendId);
        Set<Film> commonList = listOfUserFilms.stream().filter(listOfFriendFilms::contains).collect(Collectors.toSet());
        return new ArrayList<>(commonList);
    }
}
