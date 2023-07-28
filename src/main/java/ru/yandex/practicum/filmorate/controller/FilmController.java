package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final UserService userService;


    @PostMapping()
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @GetMapping()
    public Object getAllFilms() {
        return filmService.returnAllFilms();
    }

    @GetMapping("/{id}")
    public Object getFilms(@PathVariable Integer id) {
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


}
