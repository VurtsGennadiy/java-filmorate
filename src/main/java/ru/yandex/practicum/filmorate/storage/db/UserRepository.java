package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

@Repository
@Primary
@RequiredArgsConstructor
@Slf4j
public class UserRepository implements UserStorage {
    private final NamedParameterJdbcOperations jdbc;
    private final RowMapper<User> userRowMapper;

    @Override
    public User create(User user) {
        String sql = """
                INSERT INTO users (email, login, name, birthday)
                VALUES (:email, :login, :name, :birthday)""";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", user.getEmail());
        params.addValue("login", user.getLogin());
        params.addValue("name", user.getName());
        params.addValue("birthday", user.getBirthday());

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder);
        user.setId(keyHolder.getKeyAs(Integer.class));
        log.info("Создан новый пользователь id = {}", user.getId());
        return user;
    }


    @Override
    public User update(User user) {
        String sql = """
                UPDATE users
                SET email = :email,
                    login = :login,
                    name = :name,
                    birthday = :birthday
                WHERE user_id = :user_id""";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", user.getEmail());
        params.addValue("login", user.getLogin());
        params.addValue("name", user.getName());
        params.addValue("birthday", user.getBirthday());
        params.addValue("user_id", user.getId());

        jdbc.update(sql, params);
        log.info("Обновлены данные пользователя id = {}", user.getId());
        return user;
    }

    @Override
    public void remove(Integer id) {
        log.trace("Удаление пользователя id = {}", id);
        String sql = "DELETE FROM users WHERE user_id = :user_id";
        MapSqlParameterSource params = new MapSqlParameterSource("user_id", id);
        if (jdbc.update(sql, params) > 0) {
            log.info("Удалён пользователь id = {}", id);
        } else {
            log.warn("Не удалось удалить пользователя id = {}", id);
        }
    }

    @Override
    public Optional<User> getUser(Integer id) {
        String sql = "SELECT * FROM users WHERE user_id = :user_id";
        MapSqlParameterSource params = new MapSqlParameterSource("user_id", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, params, userRowMapper));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> getUser(String email) {
        String sql = "SELECT * FROM users WHERE email = :email";
        MapSqlParameterSource params = new MapSqlParameterSource("email", email);
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, params, userRowMapper));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<User> getUsers() {
        String sql = "SELECT * FROM users";
        return jdbc.query(sql, userRowMapper);
    }

    @Override
    public Collection<User> getUsers(List<Integer> ids) {
        StringJoiner idJoiner = new StringJoiner(", ");
        ids.stream().map(Object::toString).forEach(idJoiner::add);
        String sql = String.format("SELECT * FROM users WHERE user_id IN (%s)", idJoiner);
        return jdbc.query(sql, userRowMapper);
    }
}
