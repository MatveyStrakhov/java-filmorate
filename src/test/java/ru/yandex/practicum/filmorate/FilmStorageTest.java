package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.DirectorDao;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmStorageTest {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DirectorDao directorDao;
    private final Director director = new Director(1, "director");
    private final Set<Director> directors = Collections.singleton(director);
    private final User user = User.builder()
            .login("login")
            .email("somemail@email.com")
            .birthday(LocalDate.of(2021, 12, 19))
            .name("toddler")
            .build();
    private final Film film = Film.builder()
            .name("kobayashi")
            .description("abs")
            .duration(1)
            .directors(directors)
            .releaseDate(LocalDate.of(2021, 1, 21))
            .mpa(Rating.builder().id(1).name("G").build())
            .genres(Set.of(Genre.builder().id(1).name("G").build()))
            .build();
    private final Film filmUpdated = Film.builder()
            .id(1)
            .name("kobayashi")
            .description("absence")
            .duration(1)
            .directors(directors)
            .releaseDate(LocalDate.of(2019, 1, 21))
            .mpa(Rating.builder().id(1).name("G").build())
            .genres(Set.of(Genre.builder().id(1).build()))
            .build();
    private final Film film2 = Film.builder()
            .name("Inquisitor")
            .description("sinner")
            .duration(1)
            .directors(directors)
            .releaseDate(LocalDate.of(2020, 1, 21))
            .mpa(Rating.builder().id(1).name("G").build())
            .genres(Set.of(Genre.builder().id(1).build()))
            .build();

    @Test
    @Order(1)
    void createFilmTest() {
        directorDao.createDirector(director);
        assertThat(filmStorage.createFilm(film))
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "kobayashi")
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("description", "abs")
                .hasFieldOrPropertyWithValue("directors", directors);
    }

    @Order(2)
    @Test
    void returnAllFilmsTest() {
        assertThat(filmStorage.returnAllFilms())
                .isNotNull()
                .hasSize(1);
    }

    @Test
    @Order(3)
    void updateFilmTest() {
        assertThat(filmStorage.updateFilm(filmUpdated))
                .isNotNull()
                .hasFieldOrPropertyWithValue("description", "absence")
                .hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    @Order(4)
    void getFilmByIdTest() {
        assertThat(filmStorage.getFilmById(1))
                .isNotNull()
                .hasFieldOrPropertyWithValue("description", "absence")
                .hasFieldOrPropertyWithValue("name", "kobayashi")
                .hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    @Order(5)
    void getPopularFilmsAndLikeFilmTest() {
        userStorage.createUser(user);
        filmStorage.createFilm(film2);
        filmStorage.likeFilm(1, 1);
        assertThat(filmStorage.getPopularFilms(2)).hasSize(2);
        assertThat(filmStorage.getPopularFilms(2).get(0)).isNotNull()
                .hasFieldOrPropertyWithValue("description", "absence")
                .hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    @Order(6)
    void isValidFilmTest() {
        assertThat(filmStorage.isValidFilm(1)).isTrue();
        assertThat(filmStorage.isValidFilm(999)).isFalse();
    }

    @Test
    @Order(7)
    void getFilmsByDirectorSortedByLikes() {
        assertThat(filmStorage.getFilmsByDirector(1, "likes"))
                .hasSize(2);
        assertThat(filmStorage.getFilmsByDirector(1, "likes").get(0))
                .isNotNull()
                .hasFieldOrPropertyWithValue("description", "absence")
                .hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    @Order(8)
    void getFilmsByDirectorSortedByYear() {
        assertThat(filmStorage.getFilmsByDirector(1, "year"))
                .hasSize(2);
        assertThat(filmStorage.getFilmsByDirector(1, "year").get(0))
                .isNotNull()
                .hasFieldOrPropertyWithValue("description", "sinner")
                .hasFieldOrPropertyWithValue("id", 2);
    }
}
