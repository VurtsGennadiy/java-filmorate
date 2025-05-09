package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.of(
                rs.getInt("event_id"),
                rs.getInt("user_id"),
                Event.EventType.valueOf(rs.getString("event_type")),
                Event.Operation.valueOf(rs.getString("operation")),
                rs.getLong("event_timestamp"),
                rs.getInt("entity_id")
        );
    }
}
