package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmStorageTest {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
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
            .releaseDate(LocalDate.of(2021, 1, 21))
            .mpa(Rating.builder().id(1).name("G").build())
            .genres(Set.of(Genre.builder().id(1).name("G").build()))
            .build();
    private final Film filmUpdated = Film.builder()
            .id(1)
            .name("kobayashi")
            .description("absence")
            .duration(1)
            .releaseDate(LocalDate.of(2021, 1, 21))
            .mpa(Rating.builder().id(1).name("G").build())
            .genres(Set.of(Genre.builder().id(1).build()))
            .build();
    private final Film film2 = Film.builder()
            .name("Inquisitor")
            .description("sinner")
            .duration(1)
            .releaseDate(LocalDate.of(2021, 1, 21))
            .mpa(Rating.builder().id(1).name("G").build())
            .genres(Set.of(Genre.builder().id(1).build()))
            .build();

    @Test
    @Order(1)
    void createFilmTest() {
        assertThat(filmStorage.createFilm(film))
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "kobayashi")
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("description", "abs");
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
    }

    @Test
    @Order(6)
    void isValidFilmTest() {
        assertThat(filmStorage.isValidFilm(1)).isTrue();
        assertThat(filmStorage.isValidFilm(999)).isFalse();
    }
}
