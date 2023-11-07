package ru.yandex.practicum.filmorate.service.iservice;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface IRecommendationService {

    Director getDirectorById(int directorId);

    Director updateDirector(Director director);

    List<Director> getAllDirectors();

    Director createDirector(Director director);

    void deleteDirector(int directorId);

    boolean isValidDirector(int directorId);
}
