package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Reviews;

import java.util.List;
import java.util.Optional;

public interface ReviewsStorage {

    Reviews createReviews(Reviews reviews);

    Reviews updateReviews(Reviews reviews);

    void deleteReviews(Integer id);

    Optional<Reviews> getReviewsById(Integer id);

    List<Reviews> getPopularReviews(Integer filmId, Integer count);

    void putLikes(Integer id, Integer userId);

    void putDislikes(Integer id, Integer userId);

    void deleteLikes(Integer id, Integer userId);

    void deleteDislikes(Integer id, Integer userId);
}
