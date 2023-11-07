package ru.yandex.practicum.filmorate.service.iservice;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface IGenreService {

    Genre getGenreById(int genreId);

    List<Genre> getAllGenres();
}
