package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmsExtractor;
import ru.yandex.practicum.filmorate.storage.ReviewMapper;
import ru.yandex.practicum.filmorate.storage.UserMapper;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewDao {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewMapper reviewMapper;
    private static final boolean LIKE = true;
    private static final boolean DISLIKE = false;

    public Review createReview(Review review) {
        String sqlQueryInsertFilmsReviewsTable = "INSERT INTO films_reviews(film_id, review_id) VALUES (?, ?);";
        String sqlQueryInsertUsersReviewsTable = "INSERT INTO users_reviews(user_id, review_id) VALUES (?, ?);";
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        review.setReviewId(simpleJdbcInsert.executeAndReturnKey(review.toMap()).intValue());
        jdbcTemplate.update(sqlQueryInsertFilmsReviewsTable, review.getFilmId(), review.getReviewId());
        jdbcTemplate.update(sqlQueryInsertUsersReviewsTable, review.getUserId(), review.getReviewId());
        return review;
    }

    public Review updateReview(Review review) {
        String sqlQueryUpdateReviewsTable = "UPDATE reviews SET content = ?, is_positive = ?, useful = ? "
                + "WHERE review_id = ?";
        String sqlQueryUpdateFilmsReviewsTable = "UPDATE films_reviews SET film_id = ? "
                + "WHERE review_id = ?";
        String sqlQueryUpdateUsersReviewsTable = "UPDATE users_reviews SET user_id = ? "
                + "WHERE review_id = ?";
        log.info("review update started");
        jdbcTemplate.update(sqlQueryUpdateReviewsTable, review.getContent(), review.getIsPositive(), review.getUseful(),
                review.getReviewId());
        jdbcTemplate.update(sqlQueryUpdateFilmsReviewsTable, review.getFilmId(), review.getReviewId());
        jdbcTemplate.update(sqlQueryUpdateUsersReviewsTable, review.getUserId(), review.getReviewId());
        return getReviewById(review.getReviewId());
    }

    public Review getReviewById(int reviewId) {
        String sqlQuery = "SELECT r.review_id, r.content, r.is_positive, r.useful, ur.user_id, fr.film_id " +
                    "FROM reviews AS r " +
                    "LEFT JOIN users_reviews AS ur ON ur.review_id = r.review_id " +
                    "LEFT JOIN films_reviews AS fr ON r.review_id = fr.review_id " +
                    "WHERE r.review_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, reviewMapper, reviewId);
    }

    public boolean deleteReviewById(int reviewId) {
        if (isValidReview(reviewId)) {
            String sqlQueryOnDeleteReviewInReviewsTable = "DELETE FROM reviews WHERE review_id = ?";
            String sqlQueryOnDeleteReviewInFilmsReviewsTable = "DELETE FROM films_reviews WHERE review_id = ?";
            String sqlQueryOnDeleteReviewInUsersReviewsTable = "DELETE FROM users_reviews WHERE review_id = ?";
            String sqlQueryOnDeleteReviewInLikesReviewsTable = "DELETE FROM likes_reviews WHERE review_id = ?";
            jdbcTemplate.update(sqlQueryOnDeleteReviewInReviewsTable, reviewId);
            jdbcTemplate.update(sqlQueryOnDeleteReviewInFilmsReviewsTable, reviewId);
            jdbcTemplate.update(sqlQueryOnDeleteReviewInUsersReviewsTable, reviewId);
            jdbcTemplate.update(sqlQueryOnDeleteReviewInLikesReviewsTable, reviewId);
            return true;
        } else {
            return false;
        }
    }

    public List<Review> getAllReviews(int count) {
        String sqlQuery = "SELECT r.review_id, r.content, r.is_positive, r.useful, ur.user_id, fr.film_id " +
                "FROM reviews AS r " +
                "LEFT JOIN users_reviews AS ur ON ur.review_id = r.review_id " +
                "LEFT JOIN films_reviews AS fr ON r.review_id = fr.review_id " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, reviewMapper, count);
    }

    public void likeReview(Integer reviewId, Integer userId) {
        String sql = "MERGE INTO likes_reviews (user_id, reviews_id, is_like) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, reviewId, LIKE);
    }

    public void unlikeReview(Integer reviewId, Integer userId) {
        String sql = "DELETE FROM likes_reviews WHERE user_id = ? AND review_id = ? AND is_like = ?";
        jdbcTemplate.update(sql, userId, reviewId, LIKE);
    }

    public void dislikeReview(Integer reviewId, Integer userId) {
        String sql = "MERGE INTO likes_reviews (user_id, reviews_id, is_like) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, reviewId, DISLIKE);
    }

    public void unDislikeReview(Integer reviewId, Integer userId) {
        String sql = "DELETE FROM likes_reviews WHERE user_id = ? AND review_id = ? AND is_like = ?";
        jdbcTemplate.update(sql, userId, reviewId, DISLIKE);
    }

    public boolean isValidReview(int reviewId) {
        return jdbcTemplate.queryForRowSet("SELECT review_id FROM reviews WHERE review_id=?", reviewId).next();
    }

    public List<Review> getReviewsByCount(int filmId, int count) {
        String sql = "SELECT r.review_id, r.content, r.is_positive, r.useful, ur.user_id, fr.film_id " +
                "FROM reviews AS r " +
                "LEFT JOIN users_reviews AS ur ON ur.review_id = r.review_id " +
                "LEFT JOIN films_reviews AS fr ON r.review_id = fr.review_id " +
                "WHERE fr.film_id = ? " +
                "LIMIT ?";
        List<Review> reviews = jdbcTemplate.query(sql, reviewMapper,filmId ,count);
        if (reviews != null && !reviews.isEmpty()) {
            return reviews;
        } else {
            return new ArrayList<>();
        }
    }

    public void upUseful(int reviewId) {
        String sql = "UPDATE reviews SET userful = userful + 1 "
                + "WHERE review_id = ?";
        log.debug("upUserful update started");
        jdbcTemplate.update(sql, reviewId);
    }

    public void downUseful(int reviewId) {
        String sql = "UPDATE reviews SET userful = userful - 1 "
                + "WHERE review_id = ?";
        log.debug("downUserful update started");
        jdbcTemplate.update(sql, reviewId);
    }

}