package ru.yandex.practicum.filmorate.service.iservice;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface IRatingService {

    Rating getRatingById(int ratingId);

    List<Rating> getAllRatings();
}
