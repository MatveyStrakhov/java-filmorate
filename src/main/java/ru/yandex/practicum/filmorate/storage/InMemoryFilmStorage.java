package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private int iD = 1;
    private Map<Integer, Film> films = new HashMap<>();


    @Override
    public Film createFilm(Film film) {
        film.setId(getID());
        films.put(film.getId(), film);
        log.info("New film added " + film.toString());
        return film;
    }

    @Override
    public Collection<Film> returnAllFilms() {
        return films.values();
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new IncorrectIdException("Validation failed: wrong id");
        } else {
            films.put(film.getId(), film);
            log.info("Film updated " + film.toString());
            return film;
        }
    }

    @Override
    public Film getFilmById(int filmId) {
        return films.getOrDefault(filmId, null);
    }

    private int getID() {
        return iD++;
    }
}
