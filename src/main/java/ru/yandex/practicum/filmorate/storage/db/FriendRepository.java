package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import java.util.List;

@Repository
@Primary
@RequiredArgsConstructor
public class FriendRepository implements FriendStorage {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public void add(Integer userId, Integer friendId) {
        String sql = """
                INSERT INTO friendship (user_id, friend_id)
                VALUES (:user_id, :friend_id)""";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("friend_id", friendId);

        jdbc.update(sql, params);
    }

    @Override
    public void remove(Integer userId, Integer friendId) {
        String sql = """
                DELETE FROM friendship
                WHERE (user_id = :user_id AND friend_id = :friend_id)""";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("friend_id", friendId);

        jdbc.update(sql, params);
    }

    @Override
    public List<Integer> get(Integer id) {
        String sql = """
                SELECT friend_id FROM friendship
                WHERE user_id = :user_id""";

        MapSqlParameterSource params = new MapSqlParameterSource("user_id", id);
        return jdbc.queryForList(sql, params, Integer.class);
    }
}
