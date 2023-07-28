package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    public List<Integer> getLikes(Film film) {
        return new ArrayList<>(film.getLikes());
    }

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
        film.setId(filmStorage.getID());
        filmStorage.getFilms().put(film.getId(), film);
        log.info("New film added " + film.toString());
        return film;
    }


    public Collection<Film> returnAllFilms() {
        return filmStorage.getFilms().values();
    }

    public Film updateFilm(Film film) {
        if (!filmStorage.getFilms().containsKey(film.getId())) {
            throw new IncorrectIdException("Validation failed: wrong id");
        } else {
            filmStorage.getFilms().put(film.getId(), film);
            log.info("Film updated " + film.toString());
            return film;
        }
    }


    public Film getFilmById(int filmId) {
        return filmStorage.getFilms().getOrDefault(filmId, null);
    }

}
