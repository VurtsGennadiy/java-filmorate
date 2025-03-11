package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicateDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.function.Predicate;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserStorage userStorage;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        log.trace("Запрос на создание нового пользователя.");
        checkEmailDuplicate(user.getEmail());
        checkUserName(user);
        return userStorage.add(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.trace("Запрос на обновление данных пользователя.");
        User oldEntry = userStorage.getUser(user.getId());
        if (!oldEntry.getEmail().equals(user.getEmail())) {
            checkEmailDuplicate(user.getEmail());
        }
        checkUserName(user);
        return userStorage.update(user);
    }

    @DeleteMapping("/{id}")
    public User remove(@PathVariable Integer id) {
        return userStorage.remove(id);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) {
        return userStorage.getUser(id);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.trace("Запрос на получение всех пользователей.");
        return userStorage.getUsers();
    }

    private void checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void checkEmailDuplicate(String email) {
        if (userStorage.getUsers().stream().map(User::getEmail).anyMatch(Predicate.isEqual(email))) {
            throw new DuplicateDataException("Пользователь с таким email уже существует.");
        }
    }
}
