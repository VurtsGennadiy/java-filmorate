package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Reviews;
import ru.yandex.practicum.filmorate.service.ReviewsService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewsController {
    private final ReviewsService reviewsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reviews createReviews(@Validated @RequestBody Reviews reviews) {
        return reviewsService.create(reviews);
    }

    @PutMapping
    public Reviews updateReviews(@Validated @RequestBody Reviews reviews) {
        return reviewsService.update(reviews);
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReviews(@PathVariable @Positive @NotNull Integer reviewId) {
        reviewsService.delete(reviewId);
    }

    @GetMapping("/{reviewId}")
    public Reviews getReviewsById(@PathVariable @Positive @NotNull Integer reviewId) {
        return reviewsService.getReviewsById(reviewId);
    }

    @GetMapping
    public List<Reviews> getPopularReviews(@RequestParam("filmId")  @Positive Integer filmId,
                                    @RequestParam(name = "count", defaultValue = "10") @Positive Integer count) {
        return reviewsService.getPopularReviews(filmId, count);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public void putLikes(@PathVariable @Positive @NotNull Integer reviewId, @PathVariable @Positive @NotNull Integer userId) {
        reviewsService.putLike(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public void putDislikes(@PathVariable @Positive Integer reviewId, @PathVariable @Positive Integer userId) {
        reviewsService.putDislike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public void deleteLike(@PathVariable @Positive Integer reviewId, @PathVariable @Positive Integer userId) {
        reviewsService.deleteLike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void deleteDislike(@PathVariable @Positive Integer reviewId, @PathVariable @Positive Integer userId) {
        reviewsService.deleteDislike(reviewId, userId);
    }
}
