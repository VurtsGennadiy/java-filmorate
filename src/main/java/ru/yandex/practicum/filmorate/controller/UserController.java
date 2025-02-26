package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicateDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users;
    private Integer id;

    public UserController() {
        users = new HashMap<>();
        id = 0;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.trace("Запрос на создание нового пользователя.");
        checkEmailDuplicate(user.getEmail());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан новый пользователь id = {}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.trace("Запрос на обновление данных пользователя.");
        Integer id = user.getId();
        checkId(id);
        if (!users.get(id).getEmail().equals(user.getEmail())) {
            checkEmailDuplicate(user.getEmail());
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Обновлены данные пользователя id = {}", user.getId());
        return user;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.trace("Запрос на получение всех пользователей.");
        return users.values();
    }

    private Integer getNextId() {
        return ++id;
    }

    private void checkEmailDuplicate(String email) {
        if (users.values().stream().map(User::getEmail).anyMatch(Predicate.isEqual(email))) {
            throw new DuplicateDataException("Пользователь с таким email уже существует.");
        }
    }

    private void checkId(Integer id) {
        if (id == null) {
            throw new ValidationException("В теле запроса необходимо указать id.");
        } else if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не существует.");
        }
    }
}
