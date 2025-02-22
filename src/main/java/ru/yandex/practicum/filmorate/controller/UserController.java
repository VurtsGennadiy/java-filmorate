package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicateDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users;
    private Integer id;

    public UserController() {
        users = new HashMap<>();
        id = 0;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        String email = user.getEmail();
        String login = user.getLogin();
        if (email != null && validateEmail(email)) {
            if (users.values().stream().map(User::getEmail).anyMatch(Predicate.isEqual(email))) {
                throw new DuplicateDataException("Пользователь с таким email уже существует.");
            }
        } else {
            throw new ValidationException("Не валидный email");
        }
        if (login == null || !validateLogin(login)) {
            throw new ValidationException("Логин не должен быть пустой или содержать пробелы.");
        }
        if (user.getName() == null || !validateName(user.getName())) {
            user.setName(login);
        }
        if (user.getBirthday() != null && !validateBirthday(user.getBirthday())) {
            throw new ValidationException("День рождения не может быть позже текущей даты.");
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User updUser) {
        Integer id = updUser.getId();
        if (id == null) {
            throw new ValidationException("В теле запроса необходимо указать id.");
        } else if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не существует.");
        }
        User user = users.get(id);
        String email = updUser.getEmail();
        String login = updUser.getLogin();
        String name = updUser.getName();
        LocalDate birthday = updUser.getBirthday();

        if (email != null) {
            if (validateEmail(email)) {
                if (users.values().stream().map(User::getEmail).anyMatch(Predicate.isEqual(email))) {
                    throw new DuplicateDataException("Пользователь с таким email уже существует.");
                }
                user.setEmail(email);
            } else {
                throw new ValidationException("Не валидный email");
            }
        }
        if (login != null) {
            if (validateLogin(login)) {
                user.setLogin(login);
            } else {
                throw new ValidationException("Логин не должен быть пустой или содержать пробелы.");
            }
        }
        if (name != null) {
            if (validateName(name)) {
                user.setName(name);
            } else {
                user.setName(user.getLogin());
            }
        }
        if (birthday != null) {
            if (validateBirthday(birthday)) {
                user.setBirthday(birthday);
            }
            else {
                throw new ValidationException("День рождения не может быть позже текущей даты.");
            }
        }
        return user;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    private boolean validateEmail(String email) {
        return email.contains("@");
    }

    private boolean validateLogin(String login) {
        return !login.isBlank() && !login.contains(" ");
    }

    private boolean validateName(String name) {
        return !name.isBlank();
    }

    private boolean validateBirthday(LocalDate date) {
        return !date.isAfter(LocalDate.now());
    }

    private Integer getNextId() {
        return ++id;
    }
}
