package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final UserService userService;
    private final DirectorService directorService;


    @PostMapping()
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @GetMapping()
    public Collection<Film> getAllFilms() {
        return filmService.returnAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilms(@PathVariable Integer id) {
        if (!filmService.isValidFilm(id)) {
            throw new IdNotFoundException("This ID doesn't exist!");
        } else {
            return filmService.getFilmById(id);
        }
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable int id, @PathVariable int userId) {
        if (filmService.isValidFilm(id) && userService.isValidUser(userId))
            filmService.likeFilm(id, userId);
        else {
            throw new IdNotFoundException("This ID doesn't exist!");
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeFilm(@PathVariable int id, @PathVariable int userId) {
        if (filmService.isValidFilm(id) && userService.isValidUser(userId))
            filmService.unlikeFilm(id, userId);
        else {
            throw new IdNotFoundException("This ID doesn't exist!");
        }
    }

    @GetMapping(value = {"/popular"})
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);

    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsByDirector(@PathVariable int directorId, @RequestParam String sortBy) {
        if (directorService.isValidDirector(directorId)) {
            return filmService.getFilmsByDirector(directorId, sortBy);
        } else {
            throw new IdNotFoundException("Director id not found!");
        }
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query, @RequestParam String by) {
        if (!query.isBlank()) {
            return filmService.searchFilms(query, by);
        } else {
            return new ArrayList<>();
        }
    }

}
