package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@EqualsAndHashCode(of = {"eventId"})
@ToString
@AllArgsConstructor(staticName = "of")
public class Event {
    private Integer eventId;
    private Integer userId;
    private EventType eventType;
    private Operation operation;
    private Long timestamp;
    private Integer entityId;

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
