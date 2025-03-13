package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film remove(Integer id) {
        return filmStorage.remove(id);
    }

    public Film getFilm(Integer id) {
        return filmStorage.getFilm(id);
    }

    public Collection<Film> getAll() {
        return filmStorage.getFilms();
    }

    public Film addLike(Integer filmId, Integer userId) {
        return filmStorage.addLike(filmId, userStorage.getUser(userId));
    }

    public Film removeLike(Integer filmId, Integer userId) {
        return filmStorage.removeLike(filmId, userStorage.getUser(userId));
    }

    public Collection<Film> getPopular(Integer count) {
        return filmStorage.getAllLikes().entrySet().stream()
                .limit(count)
                .sorted((entry1, entry2) -> Integer.compare(entry2.getValue().size(), entry1.getValue().size()))
                .map(item -> filmStorage.getFilm(item.getKey()))
                .toList();
    }
}
