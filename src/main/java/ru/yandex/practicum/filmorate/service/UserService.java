package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicateDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public User create(User user) {
        checkEmailDuplicate(user.getEmail());
        checkUserName(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        User oldEntry = getUser(user.getId());
        if (!oldEntry.getEmail().equals(user.getEmail())) {
            checkEmailDuplicate(user.getEmail());
        }
        checkUserName(user);
        return userStorage.update(user);
    }

    public void remove(Integer id) {
        userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не существует"));
        userStorage.remove(id);
    }

    public User getUser(Integer id) {
        return userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не существует"));
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    private void checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void checkEmailDuplicate(String email) {
        if (userStorage.getUser(email).isPresent()) {
            throw new DuplicateDataException("Пользователь с таким email уже существует.");
        }
    }

    public Collection<Film> getRecommendedFilms(Integer userId) {
        Integer id = userStorage.getMaxCommonLikesUser(userId);
        if (id < 0) {
            id = userId;
        }
        return filmStorage.getRecommendedFilms(userId, id);
    }
}
