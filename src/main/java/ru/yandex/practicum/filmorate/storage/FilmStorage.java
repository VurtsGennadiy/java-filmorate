package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.*;

public interface FilmStorage {
    Film create(Film film);

    void remove(Integer id);

    Film update(Film film);

    Optional<Film> getFilm(Integer id);

    Collection<Film> getFilms();

    Collection<Film> getFilms(Collection<Integer> ids);

    List<Film> getFilmsByDirector(int directorId, String sortBy);

    List<Film> searchFilm(String query, String by);

    void addLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);

    List<Film> getPopular(int limit);

    List<Film> getCommonFilmsByUsers(Integer userId,  Integer friendId);
}
