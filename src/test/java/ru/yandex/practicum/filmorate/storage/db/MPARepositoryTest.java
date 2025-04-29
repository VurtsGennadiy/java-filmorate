package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mappers.MPARowMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MPARepository.class, MPARowMapper.class})
public class MPARepositoryTest {
    private final MPARepository mpaRepository;
    private static final Map<Integer, MPA> mpaRatings = new HashMap<>();

    @BeforeAll
    static void setUp() {
        mpaRatings.put(1, new MPA(1,"G"));
        mpaRatings.put(2, new MPA(2,"PG"));
        mpaRatings.put(3, new MPA(3,"PG-13"));
        mpaRatings.put(4, new MPA(4,"R"));
        mpaRatings.put(5, new MPA(5,"NC-17"));
    }

    @Test
    @DisplayName("поиск по id")
    void getById() {
        Optional<MPA> mpa = mpaRepository.getById(1);
        assertThat(mpa)
                .isPresent()
                .get()
                .isEqualTo(mpaRatings.get(1));
    }

    @Test
    @DisplayName("пустой optional если не найден")
    void getByIdEmpty() {
        Optional<MPA> empty = mpaRepository.getById(-1);
        assertThat(empty).isEmpty();
    }

    @Test
    @DisplayName("выгрузка всех MPA")
    void getAll() {
        List<MPA> allMPA = mpaRepository.getAll();
        assertThat(allMPA)
                .usingRecursiveComparison()
                .isEqualTo(mpaRatings.values());
    }
}
