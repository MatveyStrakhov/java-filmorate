package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public List<Integer> getLikes(Film film) {
        return new ArrayList<>(film.getLikes());
    }

    public boolean likeFilm(int filmId, int userId) {
        if (isValidFilm(filmId)) {
            return filmStorage.getFilmById(filmId).getLikes().add(userId);
        } else {
            throw new IdNotFoundException("This Id doesn't exist!");
        }
    }

    public boolean isValidFilm(int id) {
        return filmStorage.getFilmById(id) != null;
    }

    public boolean unlikeFilm(int filmId, int userId) {
        if (isValidFilm(filmId)) {
            return filmStorage.getFilmById(filmId).getLikes().remove(userId);
        } else {
            throw new IdNotFoundException("This Id doesn't exist!");
        }
    }


    public List<Film> getPopularFilms(int count) {
        return filmStorage.returnAllFilms().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size())).limit(count)
                .collect(Collectors.toList());
    }

}
