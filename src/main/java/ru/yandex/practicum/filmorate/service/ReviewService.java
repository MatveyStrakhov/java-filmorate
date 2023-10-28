package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.impl.ReviewDao;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewDao reviewDao;

    public void likeReview(int reviewId, int userId) {
        reviewDao.likeReview(reviewId, userId);
        reviewDao.upUseful(reviewId);
    }

    public void unlikeReview(int reviewId, int userId) {
        reviewDao.unlikeReview(reviewId, userId);
        reviewDao.downUseful(reviewId);
    }

    public void dislikeReview(int reviewId, int userId) {
        reviewDao.dislikeReview(reviewId, userId);
        reviewDao.downUseful(reviewId);
    }

    public void unDislikeReview(int reviewId, int userId) {
        reviewDao.unDislikeReview(reviewId, userId);
        reviewDao.upUseful(reviewId);
    }

    public Review createReview(Review review) {
        return reviewDao.createReview(review);
    }

    public boolean isValidReviewById(int id) {
        return reviewDao.isValidReview(id);
    }

    public Review getReviewById(int id) {
        if (isValidReviewById(id)) {
            return reviewDao.getReviewById(id);
        } else {
            throw new IdNotFoundException("This id doesn't exist!");
        }
    }

    public Review updateReview(Review review) {
        if (isValidReviewById(review.getReviewId())) {
            return reviewDao.updateReview(review);
        } else {
            throw new IdNotFoundException("This id doesn't exist!");
        }
    }

    public void deleteReviewById(int id) {
        if (isValidReviewById(id)) {
            reviewDao.deleteReviewById(id);
        } else {
            throw new IdNotFoundException("This id doesn't exist!");
        }
    }

    public Collection<Review> returnReviewByCount(int count) {
        return reviewDao.getAllReviews(count);
    }

    public List<Review> getReviewByFilmIdAndByCount(int filmId, int count) {
        return reviewDao.getReviewsByCount(filmId, count);
    }

}
