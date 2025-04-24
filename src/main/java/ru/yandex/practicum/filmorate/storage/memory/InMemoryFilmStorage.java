package ru.yandex.practicum.filmorate.storage.memory;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final InMemoryUserStorage userStorage;
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
    public void addLike(Integer filmId, Integer userId) {
        log.trace("Поставить лайк фильму id = {} пользователь id = {}", filmId, userId);
        checkId(filmId); // ??
        User user = userStorage.getUser(userId).get();
        likes.get(filmId).add(user);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        log.trace("Удалить лайк фильма id = {} пользователь id = {}", filmId, userId);
        checkId(filmId);
        User user = userStorage.getUser(userId).get();
        likes.get(filmId).remove(user);
    }

    @Override
    public List<Film> getPopular(int limit) {
        return likes.entrySet().stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getValue().size(), entry1.getValue().size()))
                .limit(limit)
                .map(item -> getFilm(item.getKey()).orElseThrow())
                .toList();
    }

    private void checkId(Integer id) {
        if (id == null) {
            throw new ValidationException("В теле запроса необходимо указать id.");
        } else if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не существует.");
        }
    }

    private Integer getNextId() {
        return ++idCounter;
    }
}
