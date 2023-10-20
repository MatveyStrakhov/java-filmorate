package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDao;

import java.util.List;

@Service
@RequiredArgsConstructor

public class GenreService {
    private final GenreDao genreDao;

    public Genre getGenreById(int genreId) {
        if (isValidGenre(genreId)) {
            return genreDao.getGenreById(genreId);
        } else {
            throw new IdNotFoundException("This Id doesn't exist!");
        }
    }

    public List<Genre> getAllGenres() {
        return genreDao.getAllGenres();
    }

    public boolean isValidGenre(int genreId) {
        return genreDao.isValidGenre(genreId);
    }
}
