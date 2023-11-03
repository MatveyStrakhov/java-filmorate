package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.iservice.IRecommendationService;
import ru.yandex.practicum.filmorate.storage.impl.DirectorDao;

import java.util.List;

@Service
@RequiredArgsConstructor

public class DirectorService implements IRecommendationService {
    private final DirectorDao directorDao;

    @Override
    public Director getDirectorById(int directorId) {
        return directorDao.getDirectorById(directorId);
    }

    @Override
    public Director updateDirector(Director director) {
        return directorDao.updateDirector(director);
    }

    @Override
    public List<Director> getAllDirectors() {
        return directorDao.getAllDirectors();
    }

    @Override
    public Director createDirector(Director director) {
        return directorDao.createDirector(director);
    }

    @Override
    public void deleteDirector(int directorId) {
        directorDao.deleteDirector(directorId);
    }

    @Override
    public boolean isValidDirector(int directorId) {
        return directorDao.isValidDirector(directorId);
    }
}
