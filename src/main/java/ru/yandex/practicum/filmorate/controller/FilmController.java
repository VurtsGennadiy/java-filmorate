package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films;
    private Integer id;

    public FilmController() {
        films = new HashMap<>();
        id = 0;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.trace("Запрос на создание нового фильма.");
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Создан новый фильм id = {}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.trace("Запрос на обновление данных о фильме.");
        checkId(film.getId());
        films.put(film.getId(), film);
        log.info("Обновлены данные фильма id = {}", film.getId());
        return film;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.trace("Запрос на получение всех фильмов.");
        return films.values();
    }

    private Integer getNextId() {
        return ++id;
    }

    private void checkId(Integer id) {
        if (id == null) {
            throw new ValidationException("В теле запроса необходимо указать id.");
        } else if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не существует.");
        }
    }
}
