package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    public static final int DESCRIPTION_MAX_LENGTH = 200;
    public static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films;
    private Integer id;

    public FilmController() {
        films = new HashMap<>();
        id = 0;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName() == null || !validateName(film.getName())) {
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription() != null) {
            if (!validateDescription(film.getDescription())) {
                throw new ValidationException("Длина описания должна быть меньше " + DESCRIPTION_MAX_LENGTH + " символов.");
            }
        }
        if (film.getReleaseDate() != null) {
            if (!validateReleaseDate(film.getReleaseDate())) {
                throw new ValidationException("Дата релиза не может быть раньше " + EARLIEST_RELEASE_DATE + ".");
            }
        }
        if (film.getDuration() != null) {
            if (!validateDuration(film.getDuration())) {
                throw new ValidationException("Продолжительность фильма должна быть положительная.");
            }
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film updFilm) {
        Integer id = updFilm.getId();
        if (id == null) {
            throw new ValidationException("В теле запроса необходимо указать id.");
        } else if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не существует.");
        }
        Film film = films.get(id);
        String name = updFilm.getName();
        String description = updFilm.getDescription();
        LocalDate releaseDate = updFilm.getReleaseDate();
        Duration duration = updFilm.getDuration();

        if (name != null) {
            if (validateName(name)) {
                film.setName(name);
            } else {
                throw new ValidationException("Название фильма не может быть пустым.");
            }
        }
        if (description != null) {
            if (validateDescription(description)) {
                film.setDescription(description);
            } else {
                throw new ValidationException("Длина описания должна быть меньше " + DESCRIPTION_MAX_LENGTH + " символов.");
            }
        }
        if (releaseDate != null) {
            if (validateReleaseDate(releaseDate)) {
                film.setReleaseDate(releaseDate);
            }
            else {
                throw new ValidationException("Дата релиза не может быть раньше " + EARLIEST_RELEASE_DATE + ".");
            }
        }
        if (duration != null) {
            if (validateDuration(duration)) {
                film.setDuration(duration);
            } else {
                throw new ValidationException("Продолжительность фильма должна быть положительная.");
            }
        }
        return updFilm;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    private Integer getNextId() {
        return ++id;
    }

    private boolean validateName(String name) {
        return !name.isBlank();
    }

    private boolean validateReleaseDate(LocalDate date) {
        return !date.isBefore(EARLIEST_RELEASE_DATE);
    }

    private boolean validateDescription(String description) {
        return description.length() <= DESCRIPTION_MAX_LENGTH;
    }

    private boolean validateDuration(Duration duration) {
        return duration.isPositive();
    }
}
