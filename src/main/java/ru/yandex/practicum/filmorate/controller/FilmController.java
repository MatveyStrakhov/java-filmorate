package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
@Slf4j

@RestController
public class FilmController {
    private int ID = 0;
    HashMap<Integer,Film> films = new HashMap<>();
    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film){
        film.setId(getID());
        films.put(film.getId(),film);
        log.info("New film added "+film.toString());
        return film;

    }
    @GetMapping("/films")
    public Set<Map.Entry<Integer,Film>> getFilms(){
        return films.entrySet();
    }
    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film){
        if(film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))||
                film.getDescription().length()>200){
            log.error("Validation failed");
            throw new ValidationException();

        }
        else{
            films.put(film.getId(),film);
            log.info("Film updated "+film.toString());
            return film;
        }
    }
    private int getID() {
        return ID++;
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private void exceptionHandler(){
        log.error("Validation failed");
        throw new ValidationException();
    }

}
