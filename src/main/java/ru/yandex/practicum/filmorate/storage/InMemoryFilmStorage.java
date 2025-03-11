package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer id = 0;

    @Override
    public Film add(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Создан новый фильм id = {}", film.getId());
        return film;
    }

    @Override
    public Film remove(Integer id) {
        checkId(id);
        Film film = films.remove(id);
        log.info("Удалён фильм id = {}", id);
        return film;
    }

    @Override
    public Film getFilm(Integer id) {
        checkId(id);
        return films.get(id);
    }

    @Override
    public Film update(Film film) {
        checkId(id);
        films.put(film.getId(), film);
        log.info("Обновлены данные фильма id = {}", film.getId());
        return film;
    }

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    private void checkId(Integer id) {
        if (id == null) {
            throw new ValidationException("В теле запроса необходимо указать id.");
        } else if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не существует.");
        }
    }

    private Integer getNextId() {
        return ++id;
    }
}
