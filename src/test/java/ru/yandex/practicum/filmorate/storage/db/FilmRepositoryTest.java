package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@Sql(scripts = {"/test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmRepository.class, FilmRowMapper.class})
public class FilmRepositoryTest {
    private final NamedParameterJdbcOperations jdbc;
    private final FilmRepository filmRepository;
    private static final List<Film> testFilms = new ArrayList<>();
    private static final Film testFilm1 = new Film();
    private static final Film testFilm2 = new Film();
    private static final User testUser = new User();


    @BeforeAll
    static void setUp() {
        testFilm1.setId(1);
        testFilm1.setName("Джависты");
        testFilm1.setDescription("Начинающий джавист пытается протестировать jdbc репозиторий");
        testFilm1.setReleaseDate(LocalDate.of(2025, 4, 26));
        testFilm1.setDuration(100);
        testFilm1.setMpa(new MPA(3, "PG-13"));
        testFilm1.setGenres(Set.of(new Genre(1, "Комедия")));

        testFilm2.setId(2);
        testFilm2.setName("Путь к IT");
        testFilm2.setDescription("Очередной вкатун проходит курс программирования");
        testFilm2.setReleaseDate(LocalDate.of(2025, 4, 26));
        testFilm2.setDuration(10000);
        testFilm2.setMpa(new MPA(5, "NC-17"));
        testFilm2.setGenres(Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")));

        testUser.setId(1);
        testUser.setLogin("tester1");
        testUser.setName("Иван Тестов 1");
        testUser.setEmail("tester1@practicum.ru");
        testUser.setBirthday(LocalDate.of(1995, 4,25));

        testFilms.add(testFilm1);
        testFilms.add(testFilm2);
    }

    @Test
    @DisplayName("поиск по id")
    void findById() {
        Optional<Film> findFilmOptional = filmRepository.getFilm(2);
        assertThat(findFilmOptional)
                .isPresent()
                .get()
                .isEqualTo(testFilm2);
    }

    @Test
    @DisplayName("пустой optional если фильм не найден по id")
    void findByIdEmpty() {
        Optional<Film> findFilmOptional = filmRepository.getFilm(-1);
        assertThat(findFilmOptional).isEmpty();
    }

    @Test
    @DisplayName("найти все фильмы")
    void findAll() {
        Collection<Film> actual = filmRepository.getFilms();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(testFilms);
    }

    @Test
    @DisplayName("добавить новый фильм")
    void addFilm() {
       Film newFilm = new Film();
       newFilm.setName("new film test");
       newFilm.setDescription("new film description");
       newFilm.setReleaseDate(LocalDate.now());
       newFilm.setDuration(1);
       newFilm.setMpa(new MPA(1, "G"));
       newFilm.setGenres(Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")));

       Film returned = filmRepository.create(newFilm);
       Optional<Film> fromStorage = filmRepository.getFilm(returned.getId());

       assertThat(returned).isEqualTo(newFilm);
       assertThat(fromStorage)
               .isPresent()
               .get()
               .isEqualTo(newFilm);
    }

    @Test
    @DisplayName("обновить фильм")
    void updateFilm() {
        Film forUpdate = new Film();
        forUpdate.setId(1);
        forUpdate.setName("update film test");
        forUpdate.setDescription("update film description");
        forUpdate.setReleaseDate(LocalDate.now());
        forUpdate.setDuration(1);
        forUpdate.setMpa(new MPA(1, "G"));

        Film returned = filmRepository.update(forUpdate);
        Optional<Film> fromStorage = filmRepository.getFilm(forUpdate.getId());

        assertThat(returned).isEqualTo(forUpdate);
        assertThat(fromStorage)
                .isPresent()
                .get()
                .isEqualTo(forUpdate);
    }

    @Test
    @DisplayName("удалить фильм")
    void removeFilm() {
        int filmId = 1;
        assertThat(filmRepository.getFilm(filmId)).isPresent();

        filmRepository.remove(filmId);
        assertThat(filmRepository.getFilm(filmId)).isEmpty();
    }

    @Test
    @DisplayName("поставить лайк")
    void addLike() {
        int filmId = 2;
        int userId = 1;
        assertEquals(0, getLikesCount(filmId));

        filmRepository.addLike(filmId, userId);
        assertEquals(1, getLikesCount(filmId));
    }

    @Test
    @DisplayName("удалить лайк")
    void removeLike() {
        int filmId = 1;
        int userId = 1;
        assertEquals(1, getLikesCount(filmId));

        filmRepository.removeLike(filmId, userId);
        assertEquals(0, getLikesCount(filmId));
    }

    private Integer getLikesCount(Integer filmId) {
        String sql = "SELECT COUNT(user_id) FROM likes WHERE film_id = :film_id";
        MapSqlParameterSource params = new MapSqlParameterSource("film_id", filmId);
        return jdbc.queryForObject(sql, params, Integer.class);
    }

    @Test
    @DisplayName("топ популярных фильмов")
    void getPopular() {
        assertThat(filmRepository.getPopular(1, null, null)).isEqualTo(List.of(testFilm1));

        filmRepository.addLike(2, 1);
        filmRepository.addLike(2, 2);
        assertThat(filmRepository.getPopular(2, null, null))
                .isEqualTo(List.of(testFilm2, testFilm1));
    }
}
