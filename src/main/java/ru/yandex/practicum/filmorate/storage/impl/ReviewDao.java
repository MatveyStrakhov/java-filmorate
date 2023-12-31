package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewMapper;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewDao implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final ReviewMapper reviewMapper;
    private final EventDao eventDao;
    private static final boolean LIKE = true;
    private static final boolean DISLIKE = false;

    @Override
    public Review createReview(Review review) {
        String sqlQueryInsertFilmsReviewsTable = "INSERT INTO films_reviews(film_id, review_id) VALUES (?, ?);";
        String sqlQueryInsertUsersReviewsTable = "INSERT INTO users_reviews(user_id, review_id) VALUES (?, ?);";
        String sqlQueryUpdateReviewsTable = "UPDATE reviews SET is_positive = ? WHERE review_id = ?";
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        review.setReviewId(simpleJdbcInsert.executeAndReturnKey(review.toMap()).intValue());
        jdbcTemplate.update(sqlQueryUpdateReviewsTable, review.getIsPositive(), review.getReviewId());
        jdbcTemplate.update(sqlQueryInsertFilmsReviewsTable, review.getFilmId(), review.getReviewId());
        jdbcTemplate.update(sqlQueryInsertUsersReviewsTable, review.getUserId(), review.getReviewId());
        eventDao.eventAdd(review.getReviewId(), "REVIEW", "ADD", review.getUserId());
        return getReviewById(review.getReviewId());
    }

    @Override
    public Review updateReview(Review review) {
        String sqlQueryUpdateReviewsTable = "UPDATE reviews SET content = ?, is_positive = ? " +
                "WHERE review_id = ?";
        log.info("review update started");
        jdbcTemplate.update(sqlQueryUpdateReviewsTable, review.getContent(), review.getIsPositive(),
                review.getReviewId());
        Review reviewById = getReviewById(review.getReviewId());
        eventDao.eventAdd(review.getReviewId(), "REVIEW", "UPDATE", reviewById.getUserId());
        return reviewById;
    }

    @Override
    public Review getReviewById(int reviewId) {
        String sqlQuery = "SELECT r.review_id, r.content, r.is_positive, r.useful, ur.user_id, fr.film_id " +
                    "FROM reviews AS r " +
                    "LEFT JOIN users_reviews AS ur ON ur.review_id = r.review_id " +
                    "LEFT JOIN films_reviews AS fr ON r.review_id = fr.review_id " +
                    "WHERE r.review_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, reviewMapper, reviewId);
    }

    @Override
    public void deleteReviewById(int reviewId) {
        Review review = getReviewById(reviewId);
        String sqlQueryOnDeleteReviewInReviewsTable = "DELETE FROM reviews WHERE review_id = ?";
        String sqlQueryOnDeleteReviewInFilmsReviewsTable = "DELETE FROM films_reviews WHERE review_id = ?";
        String sqlQueryOnDeleteReviewInUsersReviewsTable = "DELETE FROM users_reviews WHERE review_id = ?";
        String sqlQueryOnDeleteReviewInLikesReviewsTable = "DELETE FROM likes_reviews WHERE review_id = ?";
        jdbcTemplate.update(sqlQueryOnDeleteReviewInFilmsReviewsTable, reviewId);
        jdbcTemplate.update(sqlQueryOnDeleteReviewInUsersReviewsTable, reviewId);
        jdbcTemplate.update(sqlQueryOnDeleteReviewInLikesReviewsTable, reviewId);
        jdbcTemplate.update(sqlQueryOnDeleteReviewInReviewsTable, reviewId);
        eventDao.eventAdd(review.getReviewId(), "REVIEW", "REMOVE", review.getUserId());
    }

    @Override
    public List<Review> getAllReviews(int count) {
        String sqlQuery = "SELECT r.review_id, r.content, r.is_positive, r.useful, ur.user_id, fr.film_id " +
                "FROM reviews AS r " +
                "LEFT JOIN users_reviews AS ur ON ur.review_id = r.review_id " +
                "LEFT JOIN films_reviews AS fr ON r.review_id = fr.review_id " +
                "ORDER BY r.useful DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, reviewMapper, count);
    }

    @Override
    public void likeReview(Integer reviewId, Integer userId) {
        upUseful(reviewId);
        String sql = "MERGE INTO likes_reviews (user_id, review_id, is_like) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, reviewId, LIKE);
    }

    @Override
    public void unlikeReview(Integer reviewId, Integer userId) {
        downUseful(reviewId);
        String sql = "DELETE FROM likes_reviews WHERE user_id = ? AND review_id = ? AND is_like = ?";
        jdbcTemplate.update(sql, userId, reviewId, LIKE);
    }

    @Override
    public void dislikeReview(Integer reviewId, Integer userId) {
        downUseful(reviewId);
        String sql = "MERGE INTO likes_reviews (user_id, review_id, is_like) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, reviewId, DISLIKE);
    }

    @Override
    public void unDislikeReview(Integer reviewId, Integer userId) {
        upUseful(reviewId);
        String sql = "DELETE FROM likes_reviews WHERE user_id = ? AND review_id = ? AND is_like = ?";
        jdbcTemplate.update(sql, userId, reviewId, DISLIKE);
    }

    @Override
    public boolean isValidReview(int reviewId) {
        return jdbcTemplate.queryForRowSet("SELECT review_id FROM reviews WHERE review_id=?", reviewId).next();
    }

    @Override
    public List<Review> getReviewsByCount(int filmId, int count) {
        String sql = "SELECT r.review_id, r.content, r.is_positive, r.useful, ur.user_id, fr.film_id " +
                "FROM reviews AS r " +
                "LEFT JOIN users_reviews AS ur ON ur.review_id = r.review_id " +
                "LEFT JOIN films_reviews AS fr ON r.review_id = fr.review_id " +
                "WHERE fr.film_id = ? " +
                "ORDER BY r.useful DESC " +
                "LIMIT ?";
        List<Review> reviews = jdbcTemplate.query(sql, reviewMapper, filmId, count);
        if (!reviews.isEmpty()) {
            return reviews;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void upUseful(int reviewId) {
        String sql = "UPDATE reviews SET useful = useful + 1 "
                + "WHERE review_id = ?";
        log.debug("upUseful update started");
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public void downUseful(int reviewId) {
        String sql = "UPDATE reviews SET useful = useful - 1 "
                + "WHERE review_id = ?";
        log.debug("downUseful update started");
        jdbcTemplate.update(sql, reviewId);
    }
}
