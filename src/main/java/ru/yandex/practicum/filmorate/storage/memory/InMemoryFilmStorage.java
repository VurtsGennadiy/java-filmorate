package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Set<User>> likes = new HashMap<>();
    private Integer idCounter = 0;

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        log.info("Создан новый фильм id = {}", film.getId());
        return film;
    }

    @Override
    public Film remove(Integer id) {
        checkId(id);
        Film film = films.remove(id);
        likes.remove(id);
        log.info("Удалён фильм id = {}", id);
        return film;
    }

    @Override
    public Optional<Film> getFilm(Integer id) {
        checkId(id);
        return Optional.of(films.get(id));
    }

    @Override
    public Film update(Film film) {
        checkId(film.getId());
        films.put(film.getId(), film);
        log.info("Обновлены данные фильма id = {}", film.getId());
        return film;
    }

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film addLike(Integer filmId, User user) {
        log.trace("Поставить лайк фильму id = {} пользователь id = {}", filmId, user.getId());
        checkId(filmId);
        likes.get(filmId).add(user);
        return films.get(filmId);
    }

    @Override
    public Film removeLike(Integer filmId, User user) {
        log.trace("Удалить лайк фильма id = {} пользователь id = {}", filmId, user.getId());
        checkId(filmId);
        likes.get(filmId).remove(user);
        return films.get(filmId);
    }

    @Override
    public Map<Integer, Set<User>> getAllLikes() {
        return likes;
    }

    private Integer getNextId() {
        return ++idCounter;
    }

    private void checkId(Integer id) {
        if (id == null) {
            throw new ValidationException("В теле запроса необходимо указать id.");
        } else if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не существует.");
        }
    }
}
