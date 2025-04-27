package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;


@JdbcTest
@AutoConfigureTestDatabase
@Import(FriendRepository.class)
@Sql(scripts = {"/test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendRepositoryTest {
    private final FriendRepository friendRepository;

    @Test
    @DisplayName("получить список друзей")
    void get() {
        List<Integer> expected = List.of(2);
        List<Integer> actual = friendRepository.get(1);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("добавить дружбу")
    void add() {
        assertEquals(List.of(), friendRepository.get(2));
        assertEquals(List.of(), friendRepository.get(3));
        friendRepository.add(2, 3);

        List<Integer> expectedFriendsUser2 = List.of(3);
        List<Integer> expectedFriendsUser3 = List.of();
        List<Integer> actualFriendsUser2 = friendRepository.get(2);
        List<Integer> actualFriendsUser3 = friendRepository.get(3);

        assertEquals(expectedFriendsUser2, actualFriendsUser2);
        assertEquals(expectedFriendsUser3, actualFriendsUser3);
    }

    @Test
    @DisplayName("удалить дружбу")
    void remove() {
        assertEquals(List.of(2), friendRepository.get(1));
        assertEquals(List.of(), friendRepository.get(2));

        friendRepository.remove(1, 2);
        List<Integer> actualFriendsUser1 = friendRepository.get(1);

        assertEquals(List.of(), actualFriendsUser1);
        assertEquals(List.of(), friendRepository.get(2));
    }
}
