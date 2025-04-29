package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public  Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("films.name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate((rs.getDate("release").toLocalDate()));
        film.setDuration(rs.getInt("duration"));

        MPA mpa = new MPA();
        mpa.setId(rs.getInt("films.mpa_id"));
        mpa.setName(rs.getString("mpa.name"));
        if (mpa.getId() > 0) film.setMpa(mpa);

        return film;
    }
}
