package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface FilmStorage {
    Film create(Film film);

    Film remove(Integer id);

    Film update(Film film);

    Film getFilm(Integer id);

    Collection<Film> getFilms();

    Film addLike(Integer filmId, User user);

    Film removeLike(Integer filmId, User user);

    Map<Integer, Set<User>> getAllLikes();
}
