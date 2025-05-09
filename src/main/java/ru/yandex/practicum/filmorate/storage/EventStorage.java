package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;
import java.util.Optional;

public interface EventStorage {
    Event createEvent(Event event);

    Collection<Event> getEventFeedById(Integer userId);

    Optional<Integer> checkUserById(Integer userId);
}
