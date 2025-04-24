package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
public class FilmRepository implements FilmStorage {
    private final NamedParameterJdbcOperations jdbc;
    private final FilmRowMapper filmRowMapper;
    private final GenreRowMapper genreRowMapper;
    private final GenreStorage genreStorage;

    public static final String FIND_BY_ID_QUERY = """
            SELECT * FROM films
            LEFT OUTER JOIN mpa ON films.mpa_id = mpa.mpa_id
            WHERE film_id = :film_id""";

    public static final String FIND_FILM_GENRES = """
            SELECT * FROM genres
            WHERE genre_id
            IN (SELECT genre_id FROM film_genre WHERE film_id = :film_id)""";


    public static final String INSERT_FILM_QUERY = """
            INSERT INTO films (name, description, release, duration, mpa_id)
            VALUES (:name, :description, :release, :duration, :mpa_id)""";

    @Override
    public Optional<Film> getFilm(Integer id) {
        MapSqlParameterSource params = new MapSqlParameterSource("film_id", id);
        try {
            Film film = jdbc.queryForObject(FIND_BY_ID_QUERY, params, filmRowMapper);
            List<Genre> genres = jdbc.query(FIND_FILM_GENRES, params, genreRowMapper);
            film.setGenres(new HashSet<>(genres));
            return Optional.of(film);
        } catch (EmptyResultDataAccessException emptyResult) {
            return Optional.empty();
        }
    }

    @Override
    public Film create(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release", film.getReleaseDate());
        params.addValue("duration", film.getDuration());

        params.addValue("mpa_id", (film.getMpa() == null) ? null : film.getMpa().getId());
        jdbc.update(INSERT_FILM_QUERY, params, keyHolder);
        film.setId(keyHolder.getKeyAs(Integer.class));

        if (!film.getGenres().isEmpty()) {
            saveFilmGenres(film);
        }

        return film;
    }

    private void saveFilmGenres(Film film) {
        String SQL = """
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
        jdbc.batchUpdate(SQL, batchArgs);
    }

    @Override
    public Collection<Film> getFilms() {
        String getFilmsQuery =  """
            SELECT * FROM films
            LEFT OUTER JOIN mpa ON films.mpa_id = mpa.mpa_id""";
        String getFilmsGenresQuery = "SELECT * FROM film_genre";

        Map<Integer, Film> films = jdbc.query(getFilmsQuery, filmRowMapper)
                .stream().collect(Collectors.toMap(Film::getId, film -> film));

        if (films.isEmpty()) return films.values();

        Map<Integer, Genre> genres = genreStorage.findAll().stream()
                .collect(Collectors.toMap(Genre::getId, genre -> genre));

        List<Map<String, Object>> filmsGenres = jdbc.queryForList(getFilmsGenresQuery, new HashMap<>());

        filmsGenres.forEach(entry -> {
            Integer filmId = (Integer) entry.get("film_id");
            Integer genreId = (Integer) entry.get("genre_id");
            Film film = films.get(filmId);
            Genre genre = genres.get(genreId);
            film.getGenres().add(genre);
        });

        return films.values();
    }

    @Override
    public Film remove(Integer id) {
        return null;
    }

    @Override
    public Film update(Film film) {
        String SQL = """
                UPDATE films
                SET (name = :name,
                    description = :description,
                    release = :release,
                    duration = :duration,
                    mpa_id = :mpa_id)
                WHERE film_id = :film_id""";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", film.getId());
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        params.addValue("mpa_id", (film.getMpa() == null) ? null : film.getMpa().getId());

        jdbc.update(INSERT_FILM_QUERY, params);
        return film;
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
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
        String sql = """
                DELETE FROM likes
                WHERE (film_id = :film_id AND user_id = :user_id)""";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", filmId);
        params.addValue("user_id", userId);

        jdbc.update(sql, params);
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
        return jdbc.query(sql, params, filmRowMapper);
    }
}
