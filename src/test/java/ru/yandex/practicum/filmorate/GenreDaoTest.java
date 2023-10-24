package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.impl.GenreDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class GenreDaoTest {
    private final GenreDao genreDao;

    @Test
    void getAllGenresTest() {
        List<Genre> genres = genreDao.getAllGenres();
        assertThat(genres).isNotEmpty().hasSize(6);
    }

    @Test
    void getGenreByIdTest() {
        Genre genre = genreDao.getGenreById(1);
        assertThat(genre).isNotNull().hasFieldOrPropertyWithValue("id", 1).hasFieldOrPropertyWithValue("name", "Комедия");
    }
}
