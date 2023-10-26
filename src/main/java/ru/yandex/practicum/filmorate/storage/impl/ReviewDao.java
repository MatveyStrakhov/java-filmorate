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
    private final UserMapper userMapper;
    private final FilmsExtractor filmsExtractor;
    private final ReviewMapper reviewMapper;

    private static final boolean LIKE = true;
    private static final boolean DISLIKE = false;


// добавление нового отзыва
    public Review createReview(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        review.setReviewId(simpleJdbcInsert.executeAndReturnKey(review.toMap()).intValue());
        return review;
    }

    // обновить отзыв
    public Review updateReview(Review review) {
        String sqlQueryUpdateReviewsTable = "UPDATE reviews SET content = ?, positive = ?, userful = ? "
                + "WHERE review_id = ?";
        String sqlQueryUpdateFilmsReviewsTable = "UPDATE films_reviews SET film_id = ? "
                + "WHERE review_id = ?";
        String sqlQueryUpdateUsersReviewsTable = "UPDATE users_reviews SET user_id = ? "
                + "WHERE review_id = ?";
        log.info("review update started");
        jdbcTemplate.update(sqlQueryUpdateReviewsTable, review.getContent(), review.isPositive(), review.getUseful());
        jdbcTemplate.update(sqlQueryUpdateFilmsReviewsTable, review.getFilmId());
        jdbcTemplate.update(sqlQueryUpdateUsersReviewsTable, review.getUserId());
        return getReviewById(review.getReviewId());
    }

    //получить отзыв по идентификатору
    public Review getReviewById(int reviewId) {
        if (isValidReview(reviewId)) {
            String sqlQuery = "SELECT r.review_id, r.content, r.positive, r.useful, ur.user_id " +
                    "FROM reviews AS r " +
                    "LEFT JOIN users_reviews AS ur ON ur.review_id = r.review_id " +
                    "LEFT JOIN films_reviews AS fr ON r.review_id = fr.review_id " +
                    "WHERE r.review_id = ?";

            return jdbcTemplate.queryForObject(sqlQuery, reviewMapper, reviewId);
        } else {
            throw new IdNotFoundException("Id not found!");
        }
    }

    // удалить отзыв по идентификатору
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

    // получить все отзывы
    public List<Review> getAllReviews() {
        String sqlQuery = "SELECT r.review_id, r.content, r.positive, r.useful, ur.user_id " +
                "FROM reviews AS r " +
                "LEFT JOIN users_reviews AS ur ON ur.review_id = r.review_id " +
                "LEFT JOIN films_reviews AS fr ON r.review_id = fr.review_id";
        return jdbcTemplate.query(sqlQuery, reviewMapper);
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

    public List<Review> getReviewsByCount(int count) {
        String sql = "SELECT r.review_id, r.content, r.positive, r.useful, ur.user_id " +
                "FROM reviews AS r " +
                "LEFT JOIN users_reviews AS ur ON ur.review_id = r.review_id " +
                "LEFT JOIN films_reviews AS fr ON r.review_id = fr.review_id " +
                "LIMIT ?";
        List<Review> reviews = jdbcTemplate.query(sql, reviewMapper, count);
        if (reviews != null && !reviews.isEmpty()) {
            return reviews;
        } else {
            return new ArrayList<>();
        }
    }

}
