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
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
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
        saveFilmDirectors(film);
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
        clearFilmDirectors(film);
        saveFilmGenres(film);
        saveFilmDirectors(film);
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

    private void saveFilmDirectors(Film film) {
        if (film.getDirectors().isEmpty()) return;
        String sql = """
                INSERT INTO film_director (film_id, director_id)
                VALUES (:film_id, :director_id)""";
        MapSqlParameterSource[] batchArgs = film.getDirectors().stream()
                .map(director -> {
                    MapSqlParameterSource params = new MapSqlParameterSource();
                    params.addValue("film_id", film.getId());
                    params.addValue("director_id", director.getId());
                    return params;
                })
                .toArray(MapSqlParameterSource[]::new);
        jdbc.batchUpdate(sql, batchArgs);
        log.trace("Сохранены режиссёры фильма id = {}", film.getId());
    }

    private void clearFilmGenres(Film film) {
        String clearFilmGenresSql = "DELETE from film_genre WHERE film_id = :film_id";
        MapSqlParameterSource params = new MapSqlParameterSource("film_id", film.getId());
        jdbc.update(clearFilmGenresSql, params);
        log.trace("Очищены жанры фильма id = {}", film.getId());
    }

    private void clearFilmDirectors(Film film) {
        String sql = "DELETE FROM film_director WHERE film_id = :film_id";
        MapSqlParameterSource params = new MapSqlParameterSource("film_id", film.getId());
        jdbc.update(sql, params);
        log.trace("Очищены режиссёры фильма id = {}", film.getId());
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
            connectDirectors(List.of(film));
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
        connectDirectors(films);
        return films;
    }

    public Collection<Film> getFilms(Collection<Integer> ids) {
        String sql = """
            SELECT * FROM films
            LEFT OUTER JOIN mpa ON films.mpa_id = mpa.mpa_id
            WHERE film_id IN (:film_ids)""";
        MapSqlParameterSource params = new MapSqlParameterSource("film_ids", ids);
        Collection<Film> films = jdbc.query(sql, params, filmRowMapper);
        connectGenres(films);
        connectDirectors(films);
        return films;
    }

    @Override
    public List<Film> getPopular(Integer count, Integer genreId, Integer year) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder sql = new StringBuilder("""
        SELECT f.film_id
        FROM films f
        LEFT JOIN film_genre fg ON f.film_id = fg.film_id
        LEFT JOIN likes l ON f.film_id = l.film_id
        WHERE 1=1
    """);

        if (genreId != null) {
            sql.append(" AND fg.genre_id = :genreId");
            params.addValue("genreId", genreId);
        }
        if (year != null) {
            sql.append(" AND EXTRACT(YEAR FROM f.release) = :year");
            params.addValue("year", year);
        }
        sql.append(" GROUP BY f.film_id ORDER BY COUNT(l.user_id) DESC");
        if (count != null) {
            sql.append(" LIMIT :count");
            params.addValue("count", count);
        }

        List<Integer> filmIds = jdbc.queryForList(sql.toString(), params, Integer.class);

        if (filmIds.isEmpty()) {
            return Collections.emptyList();
        }

        MapSqlParameterSource paramsFilms = new MapSqlParameterSource();
        paramsFilms.addValue("filmIds", filmIds);

        List<Film> films = (List<Film>) getFilms(filmIds);
        films.sort(Comparator.comparingInt(f -> filmIds.indexOf(f.getId())));

        return films;
    }

    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        String sql = "SELECT film_id FROM film_director WHERE director_id = :director_id";
        MapSqlParameterSource params = new MapSqlParameterSource("director_id", directorId);
        List<Integer> filmsIds = jdbc.queryForList(sql, params, Integer.class);
        Collection<Film> films = getFilms(filmsIds);

        if ("likes".equals(sortBy)) {
            return sortByLikes(films);
        } else if ("year".equals(sortBy)) {
            return sortByYear(films);
        }

        return new ArrayList<>(films);
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

    private void connectDirectors(Collection<Film> films) {
        String selectDirectorsSQL = """
                SELECT * FROM film_director AS fd
                LEFT JOIN directors ON fd.director_id = directors.director_id
                WHERE film_id IN (:film_ids)""";
        List<Integer> filmIds = films.stream().map(Film::getId).toList();
        MapSqlParameterSource params = new MapSqlParameterSource("film_ids", filmIds);
        SqlRowSet rs = jdbc.queryForRowSet(selectDirectorsSQL, params);

        Map<Integer, Film> filmsMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film));

        while (rs.next()) {
            int filmId = rs.getInt("film_id");
            int directorId = rs.getInt("director_id");
            String directorName = rs.getString("name");
            Director director = new Director(directorId, directorName);
            filmsMap.get(filmId).getDirectors().add(director);
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
                SELECT film_id
                FROM likes
                WHERE user_id IN (:userId, :friendId)
                GROUP BY film_id
                HAVING COUNT(DISTINCT user_id) = 2""";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("friendId", friendId);

        List<Integer> ids = jdbc.queryForList(sql, params, Integer.class);
        return sortByLikes(getFilms(ids));
    }

    @Override
    public List<Film> searchFilm(String query, String by) {
        String sqlSearch;
        String sql = """
                SELECT DISTINCT f.film_id,
                FROM films AS f
                """;
        if (by.contains("director") && by.contains("title")) {
            sqlSearch = """
                    LEFT OUTER JOIN film_director ON f.film_id = film_director.film_id
                    LEFT OUTER JOIN directors AS d ON film_director.director_id = d.director_id
                    WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%'))
                    """;
        } else if (by.contains("director")) {
            sqlSearch = """
                    LEFT OUTER JOIN film_director ON f.film_id = film_director.film_id
                    LEFT OUTER JOIN directors AS d ON film_director.director_id = d.director_id
                    WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%'))
                    """;

        } else if (by.contains("title")) {
            sqlSearch = """
                    WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%'))
                    """;
        } else {
            throw new NotFoundException("Некорректное значение параметра by, поиск может осуществлять только по " +
                    "режиссёрам, либо на названиям");
        }
        MapSqlParameterSource params = new MapSqlParameterSource("query", query);
        List<Integer> filmsId = jdbc.queryForList(sql + sqlSearch, params, Integer.class);
        return sortByLikes(getFilms(filmsId));
    }

    private List<Film> sortByLikes(Collection<Film> films) {
        String sql = """
                SELECT film_id FROM likes
                WHERE film_id IN (:filmsIds)
                GROUP BY (film_id)
                ORDER BY COUNT(user_id) DESC""";
        Map<Integer, Film> filmsMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film));
        MapSqlParameterSource params = new MapSqlParameterSource("filmsIds", filmsMap.keySet());

        List<Integer> sortedFilmsIds = jdbc.queryForList(sql, params, Integer.class);
        LinkedHashSet<Film> sortedFilms = new LinkedHashSet<>(
                sortedFilmsIds.stream()
                        .map(filmsMap::get)
                        .toList());
        sortedFilms.addAll(films);
        return new ArrayList<>(sortedFilms);
    }

    private List<Film> sortByYear(Collection<Film> films) {
        List<Film> sortedFilms = new ArrayList<>(films);
        sortedFilms.sort(Comparator.comparing(Film::getReleaseDate));
        return sortedFilms;
    }

    @Override
    public Collection<Film> getRecommendedFilms(Integer userId, Integer friendId) {
        log.trace("Найти не совпадающие фильмы пользователя id = {} и id = {}", userId, friendId);
        String sql = """
                SELECT film_id
                FROM likes
                WHERE user_id = :friendId
                AND film_id NOT IN (
                    SELECT film_id FROM likes WHERE user_id = :userId)
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("friendId", friendId);
        params.addValue("userId", userId);
        List<Integer> ids = jdbc.queryForList(sql, params, Integer.class);
        return getFilms(ids);
    }
}
