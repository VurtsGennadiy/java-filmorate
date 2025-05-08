package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Reviews;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewsRowMapper implements RowMapper<Reviews> {
    @Override
    public Reviews mapRow(ResultSet rs, int rowNum) throws SQLException {
        Reviews reviews = new Reviews();
        reviews.setReviewId(rs.getInt("reviews_id"));
        reviews.setContent(rs.getString("content"));
        reviews.setIsPositive(rs.getBoolean("is_positive"));
        reviews.setFilmId(rs.getInt("film_id"));
        reviews.setUserId(rs.getInt("user_id"));
        reviews.setUseful(rs.getInt("useful"));
        return reviews;
    }
}
