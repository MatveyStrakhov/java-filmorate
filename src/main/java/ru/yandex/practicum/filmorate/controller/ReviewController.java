package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;

    @PostMapping()
    public Review addReview(@Valid @RequestBody Review review) {
        if (review.getUserId() < 0 || review.getFilmId() < 0) {
            throw new IdNotFoundException("This ID doesn't exist!");
        }

        return reviewService.createReview(review);
    }

    @PutMapping()
    public Review updateReview(@RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable int id) {
        reviewService.deleteReviewById(id);
    }

    @GetMapping("/{id}")
    public Review getReviews(@PathVariable Integer id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping()
    public Collection<Review> getReviewsFilms(@RequestParam(defaultValue = "-1") int filmId, @RequestParam(defaultValue = "10") int count) {
        Collection<Review> reviews;
        if (filmId == -1) {
            reviews = reviewService.returnReviewByCount(count);
        } else {
            reviews = reviewService.getReviewByFilmIdAndByCount(filmId, count);
        }
        return reviews;
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeReview(@PathVariable int id, @PathVariable int userId) {
        if (reviewService.isValidReviewById(id) && userService.isValidUser(userId))
            reviewService.likeReview(id, userId);
        else {
            throw new IdNotFoundException("This ID doesn't exist!");
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeReview(@PathVariable int id, @PathVariable int userId) {
        if (reviewService.isValidReviewById(id) && userService.isValidUser(userId))
            reviewService.unlikeReview(id, userId);
        else {
            throw new IdNotFoundException("This ID doesn't exist!");
        }
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void dislikeReview(@PathVariable int id, @PathVariable int userId) {
        if (reviewService.isValidReviewById(id) && userService.isValidUser(userId))
            reviewService.dislikeReview(id, userId);
        else {
            throw new IdNotFoundException("This ID doesn't exist!");
        }
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void unDislikeReview(@PathVariable int id, @PathVariable int userId) {
        if (reviewService.isValidReviewById(id) && userService.isValidUser(userId))
            reviewService.unDislikeReview(id, userId);
        else {
            throw new IdNotFoundException("This ID doesn't exist!");
        }
    }

}
