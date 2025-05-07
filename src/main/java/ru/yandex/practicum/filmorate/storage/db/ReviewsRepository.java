package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Reviews;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewsRowMapper;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
@Slf4j
public class ReviewsRepository implements ReviewsStorage {
    private final NamedParameterJdbcTemplate jdbc;
    private final ReviewsRowMapper mapper;

    private static final int GRADE_LIKE = 1;
    private static final int GRADE_DISLIKE = -1;

    @Override
    public Reviews createReviews(Reviews reviews) {
        String sql = """
                INSERT INTO reviews (content, is_Positive, user_id, film_id)
                VALUES (:content, :isPositive, :userId, :filmId)""";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("content", reviews.getContent());
        paramSource.addValue("isPositive", reviews.getIsPositive());
        paramSource.addValue("userId", reviews.getUserId());
        paramSource.addValue("filmId", reviews.getFilmId());
        jdbc.update(sql, paramSource, keyHolder);

        reviews.setId(keyHolder.getKey().intValue());
        reviews.setUseful(0);
        log.info("Creating reviews with id: {}", reviews.getId());
        return reviews;
    }

    @Override
    public Reviews updateReviews(Reviews reviews) {
        //предполагается, что при обнавлении нельзя изменить userID и filmID
        String sql = """
                UPDATE reviews
                SET content = :content,
                    is_Positive = :isPositive
                WHERE reviews_id = :reviews_id
                """;
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("reviews_id", reviews.getContent());
        paramSource.addValue("content", reviews.getContent());
        paramSource.addValue("isPositive", reviews.getIsPositive());

        jdbc.update(sql, paramSource);
        //TODO добавить подсчет рейтинга
        return reviews;
    }

    @Override
    public void deleteReviews(Integer id) {
        String sql = """
                DELETE FROM reviews
                WHERE reviews_id = :reviews_id
                """;
        MapSqlParameterSource paramSource = new MapSqlParameterSource("reviews_id", id);
        jdbc.update(sql, paramSource);
        log.info("Удалён отзыв с id {}", id);
    }

    @Override
    public Optional<Reviews> getReviewsById(Integer id) {
        String sql = """
                SELECT * FROM reviews
                LEFT OUTER JOIN reviews ON reviews.reviews_id = reviews_id
                WHERE reviews_id = :reviews_id
                """;
        MapSqlParameterSource paramSource = new MapSqlParameterSource("reviews_id", id);
        try {
            Reviews reviews = jdbc.queryForObject(sql, paramSource, mapper);
            //TODO - обавить подсчет рейтинга
            return Optional.of(reviews);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Reviews> getPopularReviews(Integer filmId, Integer count) {
        String sql = """
                SELECT r.reviews_id, 
                    r.content, 
                    r.is_Positive, 
                    r.user_id, 
                    SUM(l.grade) 
                FROM reviews AS r
                LEFT JOIN likes_reviews AS l ON r.reviews_id = l.reviews_id
                WHERE r.film_id = :film_id
                GROUP BY r.reviews_id 
                LIMIT :count
                """;
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("film_id", filmId);
        paramSource.addValue("count", count);
        return jdbc.query(sql, paramSource, mapper);
    }

    @Override
    public void putLikes(Integer id, Integer userId) {
        log.trace("Пользователь {} ставит лайк отзыву {}", userId, id);
        String sql = """
                INSERT INTO likes_reviews (reviews_id, user_id, grade) 
                VALUES (:reviews_id, :user_id, :grade)
                """;
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("reviews_id", id);
        paramSource.addValue("user_id", userId);
        paramSource.addValue("grade", GRADE_LIKE);
        jdbc.update(sql, paramSource);
    }

    @Override
    public void putDislikes(Integer id, Integer userId) {
        log.trace("Пользователь {} ставит дизлайк отзыву {}", userId, id);
        String sql = """
                INSERT INTO likes_reviews (reviews_id, user_id, grade) 
                VALUES (:reviews_id, :userId, :grade)
                """;
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("reviews_id", id);
        paramSource.addValue("user_id", userId);
        paramSource.addValue("grade", GRADE_DISLIKE);
        jdbc.update(sql, paramSource);
    }

    @Override
    public void deleteLikes(Integer id, Integer userId) {
        log.trace("Пользователь {} удаляет лайк с отзыва {}", userId, id);
        String sql = """
                DELETE FROM likes_reviews
                WHERE (reviews_id = :reviews_id, user_id = :user_id, grade = :grade)
                """;
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("reviews_id", id);
        paramSource.addValue("user_id", userId);
        paramSource.addValue("grade", GRADE_LIKE);
        jdbc.update(sql, paramSource);
    }

    @Override
    public void deleteDislikes(Integer id, Integer userId) {
        log.trace("Пользователь {} удаляет дизлайк с отзыва {}", userId, id);
        String sql = """
                DELETE FROM likes_reviews
                WHERE (reviews_id = :reviews_id, user_id = :user_id, grade =:grade)
                """;
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("reviews_id", id);
        paramSource.addValue("user_id", userId);
        paramSource.addValue("grade", GRADE_DISLIKE);
        jdbc.update(sql, paramSource);
    }

    private Integer countUseful(Reviews reviews) {
        int count = 0;
        return count;
    }
}
