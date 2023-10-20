package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingDao;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
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

    @Test
    void isValidGenreTest() {
        assertThat(ratingDao.isValidRating(1)).isTrue();
        assertThat(ratingDao.isValidRating(999)).isFalse();
    }

}
