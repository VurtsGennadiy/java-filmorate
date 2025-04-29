package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreRepository.class, GenreRowMapper.class})
public class GenreRepositoryTest {
    private final GenreRepository genreRepository;
    private static final Map<Integer, Genre> genres = new HashMap<>();

    @BeforeAll
    static void setUp() {
        genres.put(1, new Genre(1,"Комедия"));
        genres.put(2, new Genre(2,"Драма"));
        genres.put(3, new Genre(3,"Мультфильм"));
        genres.put(4, new Genre(4,"Триллер"));
        genres.put(5, new Genre(5,"Документальный"));
        genres.put(6, new Genre(6,"Боевик"));
    }

    @Test
    @DisplayName("поиск жанра по id")
    void getById() {
        Optional<Genre> genreOptional = genreRepository.getById(1);
        assertThat(genreOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(genres.get(1));
    }

    @Test
    @DisplayName("пустой optional если жанр не найден")
    void getByIdEmpty() {
        Optional<Genre> emptyOptional = genreRepository.getById(-1);
        assertThat(emptyOptional).isEmpty();
    }

    @Test
    @DisplayName("выгрузка всех жанров")
    void getAll() {
        List<Genre> allGenres = genreRepository.getAll();
        assertThat(allGenres)
                .usingRecursiveComparison()
                .isEqualTo(genres.values());
    }

    @Test
    @DisplayName("все жанры есть в репозитории")
    void containsAll() {
        Collection<Integer> allIds = genres.keySet();
        boolean contains = genreRepository.containsAll(allIds);
        assertThat(contains).isTrue();
    }

    @Test
    @DisplayName("пустой список содержится в репозитории")
    void containsEmptyList() {
        boolean contains = genreRepository.containsAll(new ArrayList<>());
        assertThat(contains).isTrue();
    }

    @Test
    @DisplayName("один или несколько жанров нет в репозитории")
    void notContains() {
        boolean contains = genreRepository.containsAll(List.of(6, 7));
        assertThat(contains).isFalse();
    }
}
