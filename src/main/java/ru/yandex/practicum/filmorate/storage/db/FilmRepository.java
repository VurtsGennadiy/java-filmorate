package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
@Slf4j
public class FilmRepository implements FilmStorage {
    private final NamedParameterJdbcOperations jdbc;
    private final FilmRowMapper filmRowMapper;

    @Override
    public Film create(Film film) {
        String sql = """
            INSERT INTO films (name, description, release, duration, mpa_id)
            VALUES (:name, :description, :release, :duration, :mpa_id)""";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release", film.getReleaseDate());
        params.addValue("duration", film.getDuration());

        params.addValue("mpa_id", (film.getMpa() == null) ? null : film.getMpa().getId());
        jdbc.update(sql, params, keyHolder);
        film.setId(keyHolder.getKeyAs(Integer.class));

        if (!film.getGenres().isEmpty()) {
            saveFilmGenres(film);
        }
        log.info("Создан новый фильм id = {}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = """
                UPDATE films
                SET name = :name,
                    description = :description,
                    release = :release,
                    duration = :duration,
                    mpa_id = :mpa_id
                WHERE film_id = :film_id""";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", film.getId());
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        params.addValue("mpa_id", (film.getMpa() == null) ? null : film.getMpa().getId());

        jdbc.update(sql, params);
        clearFilmGenres(film);
        saveFilmGenres(film);
        log.info("Обновлены данные фильма id = {}", film.getId());
        return film;
    }

    private void saveFilmGenres(Film film) {
        String sql = """
                INSERT INTO film_genre (film_id, genre_id)
                VALUES (:film_id, :genre_id)""";
        MapSqlParameterSource[] batchArgs = film.getGenres().stream()
                .map(genre -> {
                    MapSqlParameterSource params = new MapSqlParameterSource();
                    params.addValue("film_id", film.getId());
                    params.addValue("genre_id", genre.getId());
                    return params;
                })
                .toArray(MapSqlParameterSource[]::new);
        jdbc.batchUpdate(sql, batchArgs);
        log.trace("Сохранены жанры фильма id = {}", film.getId());
    }

    private void clearFilmGenres(Film film) {
        String clearFilmGenresSql = "DELETE from film_genre WHERE film_id = :film_id";
        MapSqlParameterSource params = new MapSqlParameterSource("film_id", film.getId());
        jdbc.update(clearFilmGenresSql, params);
        log.trace("Очищены жанры фильма id = {}", film.getId());
    }

    @Override
    public Optional<Film> getFilm(Integer id) {
        String sql = """
            SELECT * FROM films
            LEFT OUTER JOIN mpa ON films.mpa_id = mpa.mpa_id
            WHERE film_id = :film_id""";
        MapSqlParameterSource params = new MapSqlParameterSource("film_id", id);

        try {
            Film film = jdbc.queryForObject(sql, params, filmRowMapper);
            connectGenres(List.of(film));
            return Optional.of(film);
        } catch (EmptyResultDataAccessException emptyResult) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Film> getFilms() {
        String sql = """
            SELECT * FROM films
            LEFT OUTER JOIN mpa ON films.mpa_id = mpa.mpa_id""";
        Collection<Film> films = jdbc.query(sql, filmRowMapper);
        connectGenres(films);
        return films;
    }

    @Override
    public List<Film> getPopular(int limit) {
        String sql = """
                SELECT * FROM films
                LEFT OUTER JOIN mpa ON films.mpa_id = mpa.mpa_id
                WHERE film_id IN (
                    SELECT film_id FROM likes
                    GROUP BY film_id
                    ORDER BY COUNT(user_id) DESC)
                LIMIT :limit""";
        MapSqlParameterSource params = new MapSqlParameterSource("limit", limit);
        List<Film> films = jdbc.query(sql, params, filmRowMapper);
        connectGenres(films);
        return films;
    }

    private void connectGenres(Collection<Film> films) {
        String selectGenresSQL = """
               SELECT * FROM film_genre AS fg
               LEFT JOIN genres ON fg.genre_id = genres.genre_id
               WHERE film_id IN (:film_ids)""";
        List<Integer> filmIds = films.stream().map(Film::getId).toList();
        MapSqlParameterSource params = new MapSqlParameterSource("film_ids", filmIds);
        SqlRowSet rs = jdbc.queryForRowSet(selectGenresSQL, params);

        Map<Integer, Film> filmsMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film));

        while (rs.next()) {
            int filmId = rs.getInt("film_id");
            int genreId = rs.getInt("genre_id");
            String genreName = rs.getString("name");
            Genre genre = new Genre(genreId, genreName);
            filmsMap.get(filmId).getGenres().add(genre);
        }
    }

    @Override
    public void remove(Integer id) {
        String sql = "DELETE FROM films WHERE film_id = :film_id";
        MapSqlParameterSource params = new MapSqlParameterSource("film_id", id);
        jdbc.update(sql, params);
        log.info("Удалён фильм id = {}", id);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        log.trace("Поставить лайк фильму id = {} пользователь id = {}", filmId, userId);
        String sql = """
                INSERT INTO likes (film_id, user_id)
                VALUES (:film_id, :user_id)""";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", filmId);
        params.addValue("user_id", userId);

        jdbc.update(sql, params);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        log.trace("Удалить лайк фильма id = {} пользователь id = {}", filmId, userId);
        String sql = """
                DELETE FROM likes
                WHERE (film_id = :film_id AND user_id = :user_id)""";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", filmId);
        params.addValue("user_id", userId);

        jdbc.update(sql, params);
    }

    @Override
    public List<Film> getCommonFilmsByUsers(Integer userId, Integer friendId) {
        log.trace("Найти общие фильмы пользователя id = {} и id = {}", userId, friendId);
        String sql = """
    SELECT f.film_id, f.name, f.description, f.release, f.duration,
    f.mpa_id, m.name AS mpa_name,
    g.name AS genre_name,
    COUNT(l.user_id)  AS like_count
    FROM films AS f
    LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
    LEFT JOIN film_genre AS fg ON f.film_id = fg.film_id
    LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
    JOIN likes AS l1 ON f.film_id = l1.film_id AND l1.user_id = :userId
    JOIN likes AS l2 ON f.film_id = l2.film_id AND l2.user_id = :friendId
    LEFT JOIN likes AS l ON f.film_id = l.film_id
    GROUP BY f.film_id, m.name, g.name
    """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("friendId", friendId);

        List<Film> films = jdbc.query(sql, params, filmRowMapper);
        connectGenres(films);
        return films;
    }
}
