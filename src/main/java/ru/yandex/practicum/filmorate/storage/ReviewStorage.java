package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review createReview(Review review);

    Review updateReview(Review review);

    Review getReviewById(int reviewId);

    void deleteReviewById(int reviewId);

    List<Review> getAllReviews(int count);

    void likeReview(Integer reviewId, Integer userId);

    void unlikeReview(Integer reviewId, Integer userId);

    void dislikeReview(Integer reviewId, Integer userId);

    void unDislikeReview(Integer reviewId, Integer userId);

    boolean isValidReview(int reviewId);

    List<Review> getReviewsByCount(int filmId, int count);

    void upUseful(int reviewId);

    void downUseful(int reviewId);
}
