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

    void addLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);

    List<Film> getPopular(Integer limit, Integer genreId, Integer year);

    List<Film> getCommonFilmsByUsers(Integer userId,  Integer friendId);

    Collection<Film> getRecommendedFilms(Integer userId, Integer friendId);
}
