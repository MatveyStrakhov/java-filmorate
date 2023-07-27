package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j

@RestController
@RequiredArgsConstructor
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private final UserService userService;


    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmStorage.createFilm(film);
    }

    @GetMapping(value = {"/films", "films/{id}"})
    public Object getFilms(@PathVariable(required = false) Integer id) {
        if (id == null) {
            return filmStorage.returnAllFilms();
        } else if (!filmService.isValidFilm(id)) {
            throw new IdNotFoundException("This ID doesn't exist!");
        } else {
            return filmStorage.getFilmById(id);
        }
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void likeFilm(@PathVariable int id, @PathVariable int userId) {
        if (filmService.isValidFilm(id) && userService.isValidUser(userId))
            filmService.likeFilm(id, userId);
        else {
            throw new IdNotFoundException("This ID doesn't exist!");
        }
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void unlikeFilm(@PathVariable int id, @PathVariable int userId) {
        if (filmService.isValidFilm(id) && userService.isValidUser(userId))
            filmService.unlikeFilm(id, userId);
        else {
            throw new IdNotFoundException("This ID doesn't exist!");
        }
    }

    @GetMapping(value = {"/films/popular?count={count}", "/films/popular"})
    public Collection<Film> getPopularFilms(@RequestParam(required = false) String count) {
        if (count != null) {
            try {
                int length = Integer.parseInt(count);
                return filmService.getPopularFilms(length);
            } catch (NumberFormatException e) {
                throw new IncorrectIdException("Count must be number!");
            }
        } else {
            return filmService.getPopularFilms(10);
        }

    }


}
