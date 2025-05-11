package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Reviews;

import java.util.List;
import java.util.Optional;

public interface ReviewsStorage {

    Reviews createReviews(Reviews reviews);

    Reviews updateReviews(Reviews reviews);

    void deleteReviews(Integer id);

    Optional<Reviews> getReviewsById(Integer id);

    List<Reviews> getReviewsByFilm(Integer filmId, Integer count);

    List<Reviews> getAllFilmsReviews(Integer count);

    void createLikeDislike(Integer id, Integer userId, Integer grade);

    void deleteLikeDislike(Integer id, Integer userId, Integer grade);
}
