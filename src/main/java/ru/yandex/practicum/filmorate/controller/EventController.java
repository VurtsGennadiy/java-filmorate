package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.service.EventService;

import java.util.Collection;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EventController {
    private final EventService eventService;

    @GetMapping("/{id}/feed")
    public Collection<Event> getEventFeedById(@PathVariable @Positive Integer id) {
        log.info("Запрос на получение ленты событий пользователя с id = {}", id);
        return eventService.getEventFeedById(id);
    }
}
