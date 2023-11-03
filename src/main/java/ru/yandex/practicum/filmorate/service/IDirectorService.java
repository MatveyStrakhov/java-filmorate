package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface IDirectorService {
    Director getDirectorById(int directorId);

    Director updateDirector(Director director);

    List<Director> getAllDirectors();

    Director createDirector(Director director);

    void deleteDirector(int directorId);

    boolean isValidDirector(int directorId);
}
