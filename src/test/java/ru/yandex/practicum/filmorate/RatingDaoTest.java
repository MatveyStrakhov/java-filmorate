package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.impl.RatingDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class RatingDaoTest {
    private final RatingDao ratingDao;

    @Test
    void getAllRatingsTest() {
        List<Rating> ratings = ratingDao.getAllRatings();
        assertThat(ratings).isNotEmpty().hasSize(5);
    }

    @Test
    void getGenreByIdTest() {
        Rating rating = ratingDao.getRatingById(1);
        assertThat(rating).isNotNull().hasFieldOrPropertyWithValue("id", 1).hasFieldOrPropertyWithValue("name", "G");
    }
}
