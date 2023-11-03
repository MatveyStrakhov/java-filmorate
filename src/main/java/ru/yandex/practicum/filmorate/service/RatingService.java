package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.iservice.IRatingService;
import ru.yandex.practicum.filmorate.storage.impl.RatingDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService implements IRatingService {
    private final RatingDao ratingDao;

    @Override
    public Rating getRatingById(int ratingId) {
        return ratingDao.getRatingById(ratingId);
    }

    @Override
    public List<Rating> getAllRatings() {
        return ratingDao.getAllRatings();
    }
}
