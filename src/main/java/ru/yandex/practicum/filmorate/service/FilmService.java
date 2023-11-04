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
    private final DirectorService directorService;
    private final UserService userService;

    @Override
    public void likeFilm(int filmId, int userId) {
        if (isValidFilm(filmId) && userService.isValidUser(userId))
            filmStorage.likeFilm(filmId, userId);
        else {
            throw new IdNotFoundException("This ID doesn't exist!");
        }
    }

    @Override
    public boolean isValidFilm(int id) {
        return filmStorage.isValidFilm(id);
    }

    @Override
    public void unlikeFilm(int filmId, int userId) {
        if (isValidFilm(filmId) && userService.isValidUser(userId))
            filmStorage.unlikeFilm(filmId, userId);
        else {
            throw new IdNotFoundException("This ID doesn't exist!");
        }
    }

    @Override
    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        if (directorService.isValidDirector(directorId)) {
            return filmStorage.getFilmsByDirector(directorId, sortBy);
        } else {
            throw new IdNotFoundException("Director id not found!");
        }
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
    public Film getFilmById(int id) {
        if (!isValidFilm(id)) {
            throw new IdNotFoundException("This ID doesn't exist!");
        } else {
            return filmStorage.getFilmById(id);
        }
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
        return filmStorage.findPopularFilmsFromLikes(count);
    }

    @Override
    public List<Film> findPopularFilmsFromYear(Integer count, Integer year) {
        return filmStorage.findPopularFilmsFromYear(count, year);
    }

    @Override
    public List<Film> findPopularFilmsFromGenre(Integer count, Long genreId) {
        return filmStorage.findPopularFilmsFromGenre(count, genreId);
    }

    @Override
    public List<Film> findPopularFilmsFromYearAndGenre(Integer count, Long genreId, Integer year) {
        return filmStorage.findPopularFilmsFromYearAndGenre(count, genreId, year);
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
