package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    public boolean likeFilm(int filmId, int userId) {
        if (isValidFilm(filmId)) {
            return getFilmById(filmId).getLikes().add(userId);
        } else {
            throw new IdNotFoundException("This Id doesn't exist!");
        }
    }

    public boolean isValidFilm(int id) {
        return getFilmById(id) != null;
    }

    public boolean unlikeFilm(int filmId, int userId) {
        if (isValidFilm(filmId)) {
            return getFilmById(filmId).getLikes().remove(userId);
        } else {
            throw new IdNotFoundException("This Id doesn't exist!");
        }
    }


    public List<Film> getPopularFilms(int count) {
        return returnAllFilms().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size())).limit(count)
                .collect(Collectors.toList());
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }


    public Collection<Film> returnAllFilms() {
        return filmStorage.returnAllFilms();
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }


    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId);
    }

}
