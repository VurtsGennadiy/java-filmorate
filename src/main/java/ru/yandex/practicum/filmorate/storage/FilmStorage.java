package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film add(Film film);

    Film remove(Integer id);

    Film update(Film film);

    Film getFilm(Integer id);

    Collection<Film> getFilms();
}
