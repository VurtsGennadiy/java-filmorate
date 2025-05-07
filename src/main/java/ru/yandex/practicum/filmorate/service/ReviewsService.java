package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Reviews;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewsService {
    private final ReviewsStorage reviewsStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public Reviews create(Reviews reviews) {
        log.info("Запрос на добавление пользователя");
        checkUserId(reviews.getUserId());
        checkFilmId(reviews.getFilmId());
        return reviewsStorage.createReviews(reviews);
    }

    public Reviews update(Reviews reviews) {
        return reviewsStorage.updateReviews(reviews);
    }

    @Transactional(readOnly = true)
    public Reviews getReviewsById(Integer reviewsId) {
        return reviewsStorage.getReviewsById(reviewsId)
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + reviewsId + "не был найден"));
    }

    @Transactional(readOnly = true)
    public List<Reviews> getPopularReviews(Integer filmId, Integer count) {
        checkFilmId(filmId);
        return reviewsStorage.getPopularReviews(filmId, count);
    }

    public void delete(Integer reviewsId) {
        checkReviewsId(reviewsId);
        reviewsStorage.deleteReviews(reviewsId);
    }

    public void putLike(Integer reviewsId, Integer userId) {
        reviewsStorage.putLikes(reviewsId, userId);
        Reviews review = reviewsStorage.getReviewsById(reviewsId).orElseThrow();
        reviewsStorage.updateReviews(review);
    }

    public void putDislike(Integer reviewsId, Integer userId) {
        reviewsStorage.putDislikes(reviewsId, userId);
        Reviews review = reviewsStorage.getReviewsById(reviewsId).orElseThrow();
        reviewsStorage.updateReviews(review);
    }

    public void deleteLike(Integer reviewsId, Integer userId) {
        reviewsStorage.deleteLikes(reviewsId, userId);
    }

    public void deleteDislike(Integer reviewsId, Integer userId) {
        reviewsStorage.deleteDislikes(reviewsId, userId);
    }

    private void checkUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            log.debug("Отправлен некорректный userId = {}", userId);
            throw new NotFoundException("Некорректный userId = " + userId);
        }
        userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id = " + userId + " не существует"));
    }

    private void checkFilmId(Integer filmId) {
        if (filmId == null || filmId <= 0) {
            log.debug("Отправлен некорректный filmId = {}", filmId);
            throw new NotFoundException("Некорректный filmId = " + filmId);
        }
        filmStorage.getFilm(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм id = " + filmId + " не существует"));
    }

    private void checkReviewsId(Integer reviewsId) {
        if (reviewsId == null || reviewsId <= 0) {
            log.debug("Отправлен некорректный reviewsId = {}", reviewsId);
            throw new NotFoundException("Некорректный reviewsId = " + reviewsId);
        }
    }
}
