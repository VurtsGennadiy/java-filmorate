package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@EqualsAndHashCode(of = {"eventId"})
@ToString
@AllArgsConstructor(staticName = "of")
public class Event {
    Integer eventId;
    Integer userId;
    EventType eventType;
    Operation operation;
    Long timestamp;
    Integer entityId;

    public enum EventType {
        LIKE,
        REVIEW,
        FRIEND
    }

    public enum Operation {
        ADD,
        UPDATE,
        REMOVE
    }
}
