package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.*;

@Slf4j

@RestController
public class FilmController {
    private int iD = 1;
    private Map<Integer, Film> films = new HashMap<>();


    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
            film.setId(getID());
            films.put(film.getId(), film);
            log.info("New film added " + film.toString());
            return film;
        }

    @GetMapping("/films")
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
            if (!films.containsKey(film.getId())) {
            log.warn("Validation failed: wrong id");
            throw new IncorrectIdException("Validation failed: wrong id");
        } else {
            films.put(film.getId(), film);
            log.info("Film updated " + film.toString());
            return film;
        }
    }

    private int getID() {
        return iD++;
    }

}
