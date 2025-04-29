package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;
import ru.yandex.practicum.filmorate.storage.mappers.MPARowMapper;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class MPARepository implements MPAStorage {
    private final NamedParameterJdbcOperations jdbc;
    private final MPARowMapper mapper;

    public static final String FIND_ALL_QUERY = "SELECT * FROM mpa ORDER BY mpa_id";
    public static final String FIND_MPA_BY_ID = "SELECT * FROM mpa WHERE mpa_id = :mpa_id";

    public List<MPA> getAll() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

    public Optional<MPA> getById(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("mpa_id", id);
        try {
            MPA mpa = jdbc.queryForObject(FIND_MPA_BY_ID, params, mapper);
            return Optional.ofNullable(mpa);
        } catch (EmptyResultDataAccessException emptyResult) {
            return Optional.empty();
        }
    }
}
