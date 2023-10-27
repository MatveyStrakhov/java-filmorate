package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.impl.DirectorDao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class DirectorDaoTest {
    private final DirectorDao directorDao;
    private final Director director = Director.builder()
            .directorName("New Director")
            .build();
    private final Director updatedDirector = Director.builder()
            .directorId(1)
            .directorName("Updated Director")
            .build();

    @Test
    @Order(1)
    void createDirectorTest() {
        assertThat(directorDao.createDirector(director))
                .isNotNull()
                .isInstanceOf(Director.class)
                .hasFieldOrPropertyWithValue("directorId", 1)
                .hasFieldOrPropertyWithValue("directorName", "New Director");
    }

    @Test
    @Order(2)
    void getAllDirectorsTest() {
        assertThat(directorDao.getAllDirectors())
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);
    }

    @Test
    @Order(3)
    void updateDirectorTest() {
        assertThat(directorDao.updateDirector(updatedDirector))
                .isNotNull()
                .isInstanceOf(Director.class)
                .hasFieldOrPropertyWithValue("directorId", 1)
                .hasFieldOrPropertyWithValue("directorName", "Updated Director");
    }

    @Test
    @Order(4)
    void getDirectorByIdTest() {
        assertThat(directorDao.getDirectorById(1))
                .isNotNull()
                .isInstanceOf(Director.class)
                .hasFieldOrPropertyWithValue("directorId", 1)
                .hasFieldOrPropertyWithValue("directorName", "Updated Director");
    }

    @Test
    @Order(5)
    void deleteDirectorTest() {
        directorDao.deleteDirector(1);
        assertThat(directorDao.isValidDirector(1)).isFalse();
    }

    @Test
    @Order(6)
    void getDirectorByIdFailTest() {
        assertThrows(IdNotFoundException.class, () -> directorDao.getDirectorById(1));
    }

    @Test
    @Order(7)
    void deleteDirectorFailTest() {
        assertThrows(IdNotFoundException.class, () -> directorDao.deleteDirector(1));
    }

}
