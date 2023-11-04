package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping()
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос POST /films — добавление фильма");
        return filmService.createFilm(film);
    }

    @GetMapping()
    public Collection<Film> getAllFilms() {
        log.info("Получен запрос GET /films — на получение фильмов");
        return filmService.returnAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilms(@PathVariable Integer id) {
        log.info("Получен запрос GET /films/{id} — на получение фильма по id");
        return filmService.getFilmById(id);
    }

    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос PUT /films — на обновление фильма");
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос PUT /films/{id}/like/{userId} — на то чтобы поставить лайк фильму");
        filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeFilm(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос DELETE /films/{id}/like/{userId} — на то чтобы удалить лайк у фильма");
        filmService.unlikeFilm(id, userId);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsByDirector(@PathVariable int directorId, @RequestParam String sortBy) {
        log.info("Получен запрос GET /films/director/{directorId} — на получение режисера по id");
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query, @RequestParam String by) {
        log.info("Получен запрос GET /films/search — на поиск фильма по параметрам");
        return filmService.searchFilms(query, by);
    }

    @GetMapping("/popular")
    public List<Film> findPopularFilms(@RequestParam(defaultValue = "10", required = false) @Min(1) Integer count,
                                       @RequestParam(value = "genreId", required = false) Long genreId,
                                       @RequestParam(value = "year", required = false) Integer year) {
        log.info("Получен запрос GET /films/popular?count={limit}&GenreId={GenreId}&year={год} — список популярных фильмов");
        return filmService.findPopularFilms(count, genreId, year);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Integer id) {
        log.info("Получен запрос DELETE /films/{filmId} — удален фильм");
        filmService.deleteFilm(id);
    }

    @GetMapping("/common")
    public List<Film> getPopularFilms(@RequestParam Integer userId, @RequestParam Integer friendId) {
        log.info("Получен запрос GET /films/common?userId={userId}&friendId={friendId} — список общих фильмов");
        return filmService.getCommonFilms(userId, friendId);
    }

}