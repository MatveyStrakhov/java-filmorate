package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.iservice.IGenreService;
import ru.yandex.practicum.filmorate.storage.impl.GenreDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService implements IGenreService {
    private final GenreDao genreDao;

    @Override
    public Genre getGenreById(int genreId) {
        return genreDao.getGenreById(genreId);
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreDao.getAllGenres();
    }
}
