package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmStorage filmStorage;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        log.trace("Запрос на создание нового фильма.");
        return filmStorage.add(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.trace("Запрос на обновление данных о фильме.");
        return filmStorage.update(film);
    }

    @DeleteMapping("/{id}")
    public Film remove(@PathVariable Integer id) {
        log.trace("Запрос на удаления фильма");
        return filmStorage.remove(id);
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.trace("Запрос на получение всех фильмов.");
        return filmStorage.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        return filmStorage.getFilm(id);
    }
}
