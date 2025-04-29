package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Sql(scripts = {"/test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserRepository.class, UserRowMapper.class, FriendRepository.class})
public class UserRepositoryTest {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private static final List<User> testUsers = new ArrayList<>();
    private static final User testUser1 = new User();
    private static final User testUser2 = new User();
    private static final User testUser3 = new User();

    @BeforeAll
    static void setUp() {
        testUser1.setId(1);
        testUser1.setLogin("tester1");
        testUser1.setName("Иван Тестов 1");
        testUser1.setEmail("tester1@practicum.ru");
        testUser1.setBirthday(LocalDate.of(1995, 4,25));

        testUser2.setId(2);
        testUser2.setLogin("tester2");
        testUser2.setName("Иван Тестов 2");
        testUser2.setEmail("tester2@practicum.ru");
        testUser2.setBirthday(LocalDate.of(1996, 4,25));

        testUser3.setId(3);
        testUser3.setLogin("tester3");
        testUser3.setName("Иван Тестов 3");
        testUser3.setEmail("tester3@practicum.ru");
        testUser3.setBirthday(LocalDate.of(1997, 4,26));

        testUsers.add(testUser1);
        testUsers.add(testUser2);
        testUsers.add(testUser3);
    }

    @Test
    @DisplayName("поиск по id")
    void getById() {
        Optional<User> userOptional = userRepository.getUser(1);
        assertThat(userOptional)
                .isPresent()
                .get()
                .isEqualTo(testUser1);
    }

    @Test
    @DisplayName("пустой optional если пользователь не найден по id")
    void getByIdEmpty() {
        Optional<User> userOptional = userRepository.getUser(-1);
        assertThat(userOptional).isEmpty();
    }

    @Test
    @DisplayName("поиск по email")
    void getByEmail() {
        Optional<User> userOptional = userRepository.getUser("tester1@practicum.ru");
        assertThat(userOptional)
                .isPresent()
                .get()
                .isEqualTo(testUser1);
    }

    @Test
    @DisplayName("пустой optional если пользователь не найден по email")
    void getByEmailEmpty() {
        Optional<User> userOptional = userRepository.getUser("tester0@practicum.ru");
        assertThat(userOptional).isEmpty();
    }

    @Test
    @DisplayName("добавить нового пользователя")
    void create() {
        User newUser = new User();
        newUser.setLogin("new user test");
        newUser.setName("Новый Юзер");
        newUser.setEmail("new.user@practicum.ru");
        newUser.setBirthday(LocalDate.of(2025, 4,25));

        User returned = userRepository.create(newUser);
        Optional<User> fromStorage = userRepository.getUser(returned.getId());

        assertThat(returned).isEqualTo(newUser);
        assertThat(fromStorage)
                .isPresent()
                .get()
                .isEqualTo(newUser);

    }

    @Test
    @DisplayName("обновить пользователя")
    void update() {
        User forUpdate = new User();
        forUpdate.setId(1);
        forUpdate.setLogin("updated tester");
        forUpdate.setName("Обновленный Юзер");
        forUpdate.setEmail("updated.user@practicum.ru");
        forUpdate.setBirthday(LocalDate.of(2025, 4,25));

        User returned = userRepository.update(forUpdate);
        Optional<User> fromStorage = userRepository.getUser(forUpdate.getId());

        assertThat(returned).isEqualTo(forUpdate);
        assertThat(fromStorage)
                .isPresent()
                .get()
                .isEqualTo(forUpdate);
    }

    @Test
    @DisplayName("удалить пользователя")
    void removeUser() {
        int userId = 2;
        assertThat(userRepository.getUser(userId)).isPresent();
        assertThat(friendRepository.get(1)).contains(userId);

        userRepository.remove(userId);
        assertThat(userRepository.getUser(userId)).isEmpty();
        assertThat(friendRepository.get(2)).doesNotContain(userId);
    }

    @Test
    @DisplayName("найти всех пользователей")
    void getAll() {
        Collection<User> findAll = userRepository.getUsers();
        assertThat(findAll).isEqualTo(testUsers);
    }

    @Test
    @DisplayName("найти список пользователей")
    void getUsers() {
        List<Integer> forFind = List.of(1);
        List<User> expected = List.of(testUser1);

        Collection<User> actual = userRepository.getUsers(forFind);
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
}
