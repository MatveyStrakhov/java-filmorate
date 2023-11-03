package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.iservice.IReviewService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final IReviewService reviewService;

    @PostMapping()
    public Review addReview(@Valid @RequestBody Review review) {
        log.info("create review");
        return reviewService.createReview(review);
    }

    @PutMapping()
    public Review updateReview(@RequestBody Review review) {
        log.info("update review");
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable int id) {
        log.info("delete review");
        reviewService.deleteReviewById(id);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable Integer id) {
        log.info("get review by id");
        return reviewService.getReviewById(id);
    }

    @GetMapping()
    public Collection<Review> getReviewsFilms(@RequestParam(defaultValue = "-1") int filmId,
                                              @RequestParam(defaultValue = "10") int count) {
        log.info("get reviews by filmid: " + filmId + ". with count: " + count);
        return reviewService.getReviewByFilmIdAndByCount(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeReview(@PathVariable int id, @PathVariable int userId) {
        log.info("like review with id: " + id + " with user's id: " + userId);
        reviewService.likeReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeReview(@PathVariable int id, @PathVariable int userId) {
        log.info("unlike review with id: " + id + " with user's id: " + userId);
        reviewService.unlikeReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void dislikeReview(@PathVariable int id, @PathVariable int userId) {
        log.info("dislike review with id: " + id + " with user's id: " + userId);
        reviewService.dislikeReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void unDislikeReview(@PathVariable int id, @PathVariable int userId) {
        log.info("unDislike review with id: " + id + " with user's id: " + userId);
        reviewService.unDislikeReview(id, userId);
    }
}
