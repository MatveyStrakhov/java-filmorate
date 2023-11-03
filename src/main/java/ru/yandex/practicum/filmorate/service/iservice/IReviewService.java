package ru.yandex.practicum.filmorate.service.iservice;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface IReviewService {
    void likeReview(int reviewId, int userId);

    void unlikeReview(int reviewId, int userId);

    void dislikeReview(int reviewId, int userId);

    void unDislikeReview(int reviewId, int userId);

    Review createReview(Review review);

    boolean isValidReviewById(int id);

    Review getReviewById(int id);

    Review updateReview(Review review);

    void deleteReviewById(int id);

    List<Review> getReviewByFilmIdAndByCount(int filmId, int count);
}
