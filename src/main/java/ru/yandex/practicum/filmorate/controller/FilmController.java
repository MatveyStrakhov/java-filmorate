package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@RestController
public class FilmController {
    private int ID = 0;
    HashMap<Integer,Film> films = new HashMap<>();
    @PostMapping("/films")
    public Film addFilm(@RequestBody Film film){
        film.setId(getID());
       films.put(film.getId(),film);
       return film;
    }
    @GetMapping("/films")
    public Set<Map.Entry<Integer,Film>> getFilms(){
        return films.entrySet();
    }
    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film){
        films.put(film.getId(),film);
        return film;
    }
    private int getID() {
        return ID++;
    }

}
