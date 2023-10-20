package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingDao ratingDao;

    public Rating getRatingById(int ratingId) {

        if (isValidRating(ratingId)) {
            return ratingDao.getRatingById(ratingId);
        } else {
            throw new IdNotFoundException("This Id doesn't exist!");
        }

    }

    public List<Rating> getAllRatings() {
        return ratingDao.getAllRatings();
    }

    public boolean isValidRating(int ratingId) {
        return ratingDao.isValidRating(ratingId);
    }
}
