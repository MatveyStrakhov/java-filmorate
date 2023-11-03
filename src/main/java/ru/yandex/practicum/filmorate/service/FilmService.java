package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.iservice.IFilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService implements IFilmService {
    private final FilmStorage filmStorage;


    @Override
    public void likeFilm(int filmId, int userId) {
        filmStorage.likeFilm(filmId, userId);
    }

    @Override
    public boolean isValidFilm(int id) {
        return filmStorage.isValidFilm(id);
    }

    @Override
    public void unlikeFilm(int filmId, int userId) {
        filmStorage.unlikeFilm(filmId, userId);
    }

    @Override
    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }

    @Override
    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    @Override
    public Collection<Film> returnAllFilms() {
        return filmStorage.returnAllFilms();
    }

    @Override
    public Film updateFilm(Film film) {
        if (isValidFilm(film.getId())) {
            return filmStorage.updateFilm(film);
        } else {
            throw new IdNotFoundException("This id doesn't exist!");
        }
    }

    @Override
    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId);
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        if (!query.isBlank()) {
            return filmStorage.searchFilms(query, by);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void deleteFilm(int filmId) {
        filmStorage.deleteFilm(filmId);
    }

    @Override
    public Collection<Film> getFilmsByUser(Integer userId) {
        return filmStorage.getFilmsByUser(userId);
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        Collection<Film> listOfUserFilms = getFilmsByUser(userId);
        Collection<Film> listOfFriendFilms = getFilmsByUser(friendId);
        Set<Film> commonList = listOfUserFilms.stream().filter(listOfFriendFilms::contains).collect(Collectors.toSet());
        return new ArrayList<>(commonList);
    }
}
