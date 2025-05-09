package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventStorage eventStorage;

    public void createEvent(Integer userId, Event.EventType type, Event.Operation operation, Integer entityId) {
        Event event = Event.of(
                0,
                userId,
                type,
                operation,
                Instant.now().toEpochMilli(),
                entityId
        );
        eventStorage.createEvent(event);
    }

    public Collection<Event> getEventFeedById(Integer userId) {
        if (userId == null || userId < 1) {
            log.warn("Запрос на получение ленты событий, id некорректен");
            throw new ValidationException("Лента событий не может быть получена, id = " + userId + " некорректен");
        }
        checkUserById(userId);
        return Collections.unmodifiableCollection(eventStorage.getEventFeedById(userId));
    }

    private void checkUserById(Integer userId) {
        eventStorage.checkUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не существует"));
    }
}
