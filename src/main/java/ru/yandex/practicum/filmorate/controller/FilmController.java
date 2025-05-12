package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Validated
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        filmService.create(film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Integer id) {
        filmService.remove(id);
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        return filmService.getFilm(id);
    }

    @PutMapping(value = {"/{id}/like/{userId}/{score}", "/{id}/like/{userId}"})
    public void addLike(@PathVariable Integer id,
                                        @PathVariable Integer userId,
                                        @PathVariable(required = false) Integer score) {
        filmService.addOrUpdateScore(id, userId, Objects.requireNonNullElse(score, 5));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id,
                           @PathVariable Integer userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(required = false) @Positive Integer count,
                                       @RequestParam(required = false) Integer genreId,
                                       @RequestParam(required = false) @Positive Integer year) {
        if (genreId == null && year == null) {
            if (count == null) {
                return filmService.getPopular(10);
            }
            return filmService.getPopular(count);
        }
        return filmService.getPopularByGenreAndYear(count, genreId, year);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilmsByUsers(@RequestParam Integer userId, @RequestParam Integer friendId) {
        return filmService.getCommonFilmsByUsers(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable int directorId, @RequestParam String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query, @RequestParam String by) {
        return filmService.searchFilms(query, by);
    }
}
