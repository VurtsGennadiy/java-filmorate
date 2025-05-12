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

    void addOrUpdateScore(Integer filmId, Integer userId, Double score);

    void removeLike(Integer filmId, Integer userId);

    List<Film> getPopular(Integer limit, Integer genreId, Integer year);

    List<Film> getCommonFilmsByUsers(Integer userId,  Integer friendId);

    Collection<Film> getRecommendedFilms(Integer userId, Integer friendId);
}
