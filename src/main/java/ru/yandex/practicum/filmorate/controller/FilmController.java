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
        if(film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))){
            log.warn("Validation failed: release date is too early");
            throw new ValidationException("Validation failed: release date is too early");
        }
        else if(film.getDescription().length()>200){
            log.warn("Validation failed: description is too long");
            throw new ValidationException("Validation failed: description is too long");
        }
        else if (film.getDuration().isNegative()){
            log.warn("Validation failed: duration is negative");
            throw new ValidationException("Validation failed: duration is negative");
        }
        else{
        film.setId(getID());
        films.put(film.getId(),film);
        log.info("New film added "+film.toString());
        return film;}

    }
    @GetMapping("/films")
    public Set<Map.Entry<Integer,Film>> getFilms(){
        //generateFilm();
        return films.entrySet();
    }
    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film){
        if(film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))){
            log.warn("Validation failed: release date is too early");
            throw new ValidationException("Validation failed: release date is too early");
        }
        else if(film.getDescription().length()>200){
            log.warn("Validation failed: description is too long");
            throw new ValidationException("Validation failed: description is too long");
        }
        else if (film.getDuration().isNegative()){
            log.warn("Validation failed: duration is negative");
            throw new ValidationException("Validation failed: duration is negative");
        }
        else if (!films.containsKey(film.getId())){
            log.warn("Validation failed: wrong id");
            throw new ValidationException("Validation failed: wrong id");
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
    void handleException(MethodArgumentNotValidException e){
        log.warn("Validation failed:" + e.getMessage());
        throw new ValidationException("Validation failed:" + e.getMessage());

    }


}
