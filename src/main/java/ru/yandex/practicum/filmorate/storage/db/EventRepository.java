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
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Repository
@Primary
@RequiredArgsConstructor
@Slf4j
public class EventRepository implements EventStorage {
    private final NamedParameterJdbcOperations jdbc;
    private final RowMapper<Event> eventRowMapper;

    @Override
    public Event createEvent(Event event) {
        final String sql = """
                INSERT INTO events (user_id, event_type, operation, event_timestamp, entity_id)
                VALUES (:user_id, :event_type, :operation, :event_timestamp, :entity_id);
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", event.getUserId());
        params.addValue("event_type", event.getEventType().name());
        params.addValue("operation", event.getOperation().name());
        params.addValue("event_timestamp", event.getTimestamp());
        params.addValue("entity_id", event.getEntityId());

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(sql, params, keyHolder);
        event.setEventId(keyHolder.getKeyAs(Integer.class));
        log.info("Создано новое событие для пользователя с id = {}", event.getUserId());
        return event;
    }

    @Override
    public Collection<Event> getEventFeedById(Integer userId) {
        final String sql = """
                SELECT *
                FROM events
                WHERE user_id = :userId;
                """;

        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);
        Collection<Event> eventFeed = jdbc.query(sql, params, eventRowMapper);
        log.info("Получена лента событий для пользователя с id = {}", userId);
        if (eventFeed.isEmpty()) {
            return List.of();
        }
        return eventFeed;
    }

    @Override
    public Optional<Integer> checkUserById(Integer userId) {
        final String sql = """
                SELECT user_id
                FROM users
                WHERE user_id = :user_id;
                """;

        MapSqlParameterSource params = new MapSqlParameterSource("user_id", userId);
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, params, Integer.class));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
}
