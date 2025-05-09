package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.*;

@Repository
@Primary
@RequiredArgsConstructor
public class GenreRepository implements GenreStorage {
    private final NamedParameterJdbcOperations jdbc;
    private final RowMapper<Genre> mapper;

    @Override
    public List<Genre> getAll() {
        String sql = "SELECT * FROM genres ORDER BY genre_id";
        return jdbc.query(sql, mapper);
    }

    @Override
    public Optional<Genre> getById(int id) {
        String sql = "SELECT * FROM genres WHERE genre_id = :genre_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("genre_id", id);
        try {
            Genre genre = jdbc.queryForObject(sql, params, mapper);
            return Optional.ofNullable(genre);
        } catch (EmptyResultDataAccessException emptyResult) {
            return Optional.empty();
        }
    }

    @Override
    public boolean containsAll(Collection<Integer> ids) {
        Set<Integer> setIds = new HashSet<>(ids);
        String sql = """
                SELECT COUNT(genre_id) FROM genres
                WHERE genre_id IN (:set_id)""";
        MapSqlParameterSource params = new MapSqlParameterSource("set_id", setIds);
        Integer countFound = jdbc.queryForObject(sql, params, Integer.class);
        return Objects.equals(setIds.size(), countFound);
    }
}
