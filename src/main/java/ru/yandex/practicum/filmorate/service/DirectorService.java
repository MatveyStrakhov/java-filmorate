package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.impl.DirectorDao;

import java.util.List;

@Service
@RequiredArgsConstructor

public class DirectorService {
    private final DirectorDao directorDao;

    public Director getDirectorById(int directorId) {
        return directorDao.getDirectorById(directorId);
    }

    public Director updateDirector(Director director) {
        return directorDao.updateDirector(director);
    }

    public List<Director> getAllDirectors() {
        return directorDao.getAllDirectors();
    }

    public Director createDirector(Director director) {
        return directorDao.createDirector(director);
    }

    public void deleteDirector(int directorId) {
        directorDao.deleteDirector(directorId);
    }

    public boolean isValidDirector(int directorId) {
        return directorDao.isValidDirector(directorId);
    }
}
