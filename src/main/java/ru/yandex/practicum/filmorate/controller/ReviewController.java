package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping()
    public Review addReview(@Valid @RequestBody Review review) {
        log.info("Получен запрос POST /reviews — добавление отзыва");
        return reviewService.createReview(review);
    }

    @PutMapping()
    public Review updateReview(@RequestBody Review review) {
        log.info("Получен запрос PUT /reviews — обновление отзыва");
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable int id) {
        log.info("Получен запрос DELETE /reviews/{id} — удаление отзыва по id");
        reviewService.deleteReviewById(id);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable Integer id) {
        log.info("Получен запрос GET /reviews/{id} — получение отзыва по id");
        return reviewService.getReviewById(id);
    }

    @GetMapping()
    public Collection<Review> getReviewsFilms(@RequestParam(defaultValue = "-1") int filmId,
                                              @RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос GET /reviews/{id} — получение отзывов по фильму");
        return reviewService.getReviewByFilmIdAndByCount(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeReview(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос PUT /reviews/{id}/like/{userId} — поставить лайк отзыву");
        reviewService.likeReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeReview(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос DELETE /reviews/{id}/like/{userId} — убрать лайк у отзыва");
        reviewService.unlikeReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void dislikeReview(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос PUT /reviews/{id}/like/{userId} — поставить дизлайк отзыву");
        reviewService.dislikeReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void unDislikeReview(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен запрос DELETE /reviews/{id}/like/{userId} — убрать дизлайк у отзыва");
        reviewService.unDislikeReview(id, userId);
    }
}
