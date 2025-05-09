package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;

import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DirectorRepository implements DirectorStorage {
    private final NamedParameterJdbcOperations jdbc;
    private final DirectorRowMapper directorRowMapper;

    @Override
    public Director create(Director director) {
        String sql = """
                INSERT INTO directors (name)
                VALUES (:name)""";
        MapSqlParameterSource params = new MapSqlParameterSource("name", director.getName());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(sql, params, keyHolder);
        director.setId(keyHolder.getKeyAs(Integer.class));
        log.info("Создан новый режиссёр id = {}", director.getId());
        return director;
    }

    @Override
    public Director update(Director director) {
        String sql = """
                UPDATE directors
                SET name = :name
                WHERE director_id = :director_id
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("director_id", director.getId());
        params.addValue("name", director.getName());

        if (jdbc.update(sql, params) > 0) {
            log.info("Обновлены данные режиссёра id = {}", director.getId());
        } else {
            log.warn("Не удалось обновить данные режиссёра id = {}", director.getId());
        }
        return director;
    }

    @Override
    public void remove(int directorId) {
        String sql = "DELETE FROM directors WHERE director_id = :director_id";
        MapSqlParameterSource params = new MapSqlParameterSource("director_id", directorId);
        if (jdbc.update(sql, params) > 0) {
            log.info("Удалён режиссёр id = {}", directorId);
        } else {
            log.warn("Не удалось удалить режиссёра id = {}", directorId);
        }
    }

    @Override
    public Optional<Director> getDirector(int directorId) {
        String sql = "SELECT * FROM directors WHERE director_id = :director_id";
        MapSqlParameterSource params = new MapSqlParameterSource("director_id", directorId);
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, params, directorRowMapper));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Director> getDirectors() {
        String sql = "SELECT * FROM directors";
        return jdbc.query(sql, directorRowMapper);
    }

    @Override
    public boolean containsAll(Collection<Integer> ids) {
        Set<Integer> setIds = new HashSet<>(ids);
        String sql = """
                SELECT COUNT(director_id) FROM directors
                WHERE director_id IN (:set_id)""";
        MapSqlParameterSource params = new MapSqlParameterSource("set_id", setIds);
        Integer countFound = jdbc.queryForObject(sql, params, Integer.class);
        return Objects.equals(setIds.size(), countFound);
    }
}
