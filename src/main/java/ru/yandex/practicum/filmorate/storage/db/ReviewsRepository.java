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

    private static final String GET_SQL = """
            SELECT r.reviews_id,
                    r.content,
                    r.is_Positive,
                    r.film_id,
                    r.user_id,
                    COALESCE(SUM(l.grade), 0) AS useful
                FROM reviews AS r
                LEFT JOIN likes_reviews AS l ON r.reviews_id = l.reviews_id
            """;

    @Override
    public Reviews createReviews(Reviews reviews) {
        log.info("Запрос на добавление пользователя");;
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

        reviews.setReviewId(keyHolder.getKey().intValue());
        reviews.setUseful(0);
        log.info("Creating reviews with id: {}", reviews.getReviewId());
        return reviews;
    }

    @Override
    public Reviews updateReviews(Reviews reviews) {
        log.info("Запрос на изменения комментария {}", reviews.getReviewId());
        //предполагается, что при обновлении нельзя изменить userID и filmID
        String sql = """
                UPDATE reviews
                SET content = :content,
                    is_Positive = :isPositive
                WHERE reviews_id = :reviews_id
                """;
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("reviews_id", reviews.getReviewId());
        paramSource.addValue("content", reviews.getContent());
        paramSource.addValue("isPositive", reviews.getIsPositive());

        jdbc.update(sql, paramSource);
        log.debug("Получаем отзыв после обновления");
        String sqlByNewReviews = """
                WHERE r.reviews_id = :reviews_id
                GROUP BY r.reviews_id
                """;
        MapSqlParameterSource newParamSource = new MapSqlParameterSource("reviews_id", reviews.getReviewId());
        return jdbc.queryForObject(GET_SQL + sqlByNewReviews, newParamSource, mapper);
    }

    @Override
    public void deleteReviews(Integer id) {
        log.info("Запрос на удаления отзыва {}", id);
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
        log.info("Запрос на получения отзыва {}", id);
        String sql = """
                WHERE r.reviews_id = :reviews_id
                GROUP BY r.reviews_id
                """;
        MapSqlParameterSource paramSource = new MapSqlParameterSource("reviews_id", id);
        try {
            Reviews reviews = jdbc.queryForObject(GET_SQL + sql, paramSource, mapper);
            return Optional.of(reviews);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Reviews> getReviewsByFilm(Integer filmId, Integer count) {
        log.info("Запрос на получение {} отзывов на фильм {}", count, filmId);
        String sql = """
                WHERE r.film_id = :film_id
                GROUP BY r.reviews_id
                ORDER BY useful DESC
                LIMIT :count
                """;
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("film_id", filmId);
        paramSource.addValue("count", count);
        return jdbc.query(GET_SQL + sql, paramSource, mapper);
    }

    @Override
    public List<Reviews> getAllFilmsReviews(Integer count) {
        log.info("Запрос на получение отзывов {}", count);
        String sql = """
                GROUP BY r.reviews_id
                ORDER BY useful DESC
                LIMIT :count
                """;
        MapSqlParameterSource paramSource = new MapSqlParameterSource("count", count);
        return jdbc.query(GET_SQL + sql, paramSource, mapper);
    }

    @Override
    public void createLikeDislike(Integer id, Integer userId, Integer grade) {
        log.info("Пользователь {} хочет поставить {} на отзыв {}", userId, grade == 1 ? "лайк" : "дизлайк", id);
        deleteLikeDislike(id, userId, grade == 1 ? -1 : 1);
        String sql = """
                INSERT INTO likes_reviews (reviews_id, user_id, grade)
                VALUES (:reviews_id, :user_id, :grade)
                """;
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("reviews_id", id);
        paramSource.addValue("user_id", userId);
        paramSource.addValue("grade", grade);
        jdbc.update(sql, paramSource);
    }

    @Override
    public void deleteLikeDislike(Integer id, Integer userId, Integer grade) {
        log.info("Пользователь {} хочет удалить {} на отзыв {}", userId, grade == 1 ? "лайк" : "дизлайк", id);
        String sql = """
                DELETE FROM likes_reviews
                WHERE (reviews_id = :reviews_id AND user_id = :user_id AND grade = :grade)
                """;
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("reviews_id", id);
        paramSource.addValue("user_id", userId);
        paramSource.addValue("grade", grade);
        jdbc.update(sql, paramSource);
    }
}
