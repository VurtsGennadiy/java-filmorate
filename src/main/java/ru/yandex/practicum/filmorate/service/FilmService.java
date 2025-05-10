package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MPAStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;

    public Film create(Film film) {
        if (film.getMpa() != null) {
            checkMpaExists(film.getMpa().getId());
        }
        checkGenresExists(film.getGenres());
        checkDirectorsExists(film.getDirectors());
        filmStorage.create(film);
        return film;
    }

    public Film update(Film film) {
        checkFilmExists(film.getId());
        if (film.getMpa() != null) {
            checkMpaExists(film.getMpa().getId());
        }
        checkGenresExists(film.getGenres());
        checkDirectorsExists(film.getDirectors());
        return filmStorage.update(film);
    }

    public void remove(Integer id) {
        filmStorage.remove(id);
    }

    public Film getFilm(Integer id) {
        return filmStorage.getFilm(id)
                .orElseThrow(() -> new NotFoundException("Фильм id = " + id + " не существует"));
    }

    public Collection<Film> getAll() {
        return filmStorage.getFilms();
    }

    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        checkDirectorExists(directorId);
        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }

    public void addLike(Integer filmId, Integer userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);
        filmStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getPopular(Integer count) {
        return filmStorage.getPopular(count);
    }

    public List<Film> searchFilms(String query, String by) {
        return filmStorage.searchFilm(query, by);
    }

    public Collection<Film> getCommonFilmsByUsers(Integer userId, Integer friendId) {
        checkUserExists(userId);
        checkUserExists(friendId);
        return filmStorage.getCommonFilmsByUsers(userId, friendId);
    }

    private void checkMpaExists(int mpaId) {
        mpaStorage.getById(mpaId)
                .orElseThrow(() -> new NotFoundException("Рейтинг MPA id = " + mpaId + " не существует"));
    }

    private void checkGenresExists(Set<Genre> genres) {
        boolean containsAll = genreStorage.containsAll(
                genres.stream()
                        .map(Genre::getId)
                        .collect(Collectors.toSet()));
        if (!containsAll) {
            throw new NotFoundException("Один или несколько указанных жанров не существуют");
        }
    }

    private void checkDirectorsExists(Set<Director> directors) {
        boolean containsAll = directorStorage.containsAll(
                directors.stream()
                        .map(Director::getId)
                        .collect(Collectors.toSet()));
        if (!containsAll) {
            throw new NotFoundException("Один или несколько указанных режиссёров не существуют");
        }
    }

    private void checkDirectorExists(int directorId) {
        directorStorage.getDirector(directorId)
                .orElseThrow(() -> new NotFoundException("Режиссёр id = " + directorId + " не существует"));
    }

    private void checkFilmExists(int id) {
        filmStorage.getFilm(id)
                .orElseThrow(() -> new NotFoundException("Фильм id = " + id + " не существует"));
    }

    private void checkUserExists(int userId) {
         userStorage.getUser(userId)
                 .orElseThrow(() -> new NotFoundException("Пользователь id = " + userId + " не существует"));
    }
}
