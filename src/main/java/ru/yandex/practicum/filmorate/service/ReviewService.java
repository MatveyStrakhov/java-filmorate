package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.iservice.IReviewService;
import ru.yandex.practicum.filmorate.storage.impl.ReviewDao;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService implements IReviewService {
    private final ReviewDao reviewDao;

    @Override
    public void likeReview(int reviewId, int userId) {
        reviewDao.likeReview(reviewId, userId);
    }

    @Override
    public void unlikeReview(int reviewId, int userId) {
        reviewDao.unlikeReview(reviewId, userId);
    }

    @Override
    public void dislikeReview(int reviewId, int userId) {
        reviewDao.dislikeReview(reviewId, userId);
    }

    @Override
    public void unDislikeReview(int reviewId, int userId) {
        reviewDao.unDislikeReview(reviewId, userId);
    }

    @Override
    public Review createReview(Review review) {
        if (review.getUserId() < 0 || review.getFilmId() < 0) {
            throw new IdNotFoundException("This ID doesn't exist!");
        }

        return reviewDao.createReview(review);
    }

    @Override
    public boolean isValidReviewById(int id) {
        return reviewDao.isValidReview(id);
    }

    @Override
    public Review getReviewById(int id) {
        if (isValidReviewById(id)) {
            return reviewDao.getReviewById(id);
        } else {
            throw new IdNotFoundException("This id doesn't exist!");
        }
    }

    @Override
    public Review updateReview(Review review) {
        if (isValidReviewById(review.getReviewId())) {
            return reviewDao.updateReview(review);
        } else {
            throw new IdNotFoundException("This id doesn't exist!");
        }
    }

    @Override
    public void deleteReviewById(int id) {
        if (isValidReviewById(id)) {
            reviewDao.deleteReviewById(id);
        } else {
            throw new IdNotFoundException("This id doesn't exist!");
        }
    }

    @Override
    public List<Review> getReviewByFilmIdAndByCount(int filmId, int count) {
        List<Review> reviews;
        if (filmId == -1) {
            reviews = reviewDao.getAllReviews(count);
        } else {
            reviews = reviewDao.getReviewsByCount(filmId, count);
        }
        return reviews;
    }

}
