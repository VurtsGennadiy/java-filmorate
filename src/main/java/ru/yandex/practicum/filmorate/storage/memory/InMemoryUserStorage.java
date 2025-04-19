package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer idCounter = 0;

    @Override
    public User getUser(Integer id) {
        checkId(id);
        return users.get(id);
    }

    @Override
    public User add(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан новый пользователь id = {}", user.getId());
        return user;
    }

    @Override
    public User remove(Integer id) {
        checkId(id);
        User user = users.remove(id);
        log.info("Удалён пользователь id = {}", id);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.info("Обновлены данные пользователя id = {}", user.getId());
        return user;
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    private Integer getNextId() {
        return ++idCounter;
    }

    private void checkId(Integer id) {
        if (id == null) {
            throw new ValidationException("В теле запроса необходимо указать id.");
        } else if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не существует.");
        }
    }
}
