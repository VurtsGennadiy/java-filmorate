package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.*;

public interface FilmStorage {
    Film create(Film film);

    void remove(Integer id);

    Film update(Film film);

    Optional<Film> getFilm(Integer id);

    Collection<Film> getFilms();

    void addLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);

    List<Film> getPopular(int limit);
}
