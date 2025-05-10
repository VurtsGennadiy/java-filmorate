package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User create(User user);

    void remove(Integer id);

    User update(User user);

    Optional<User> getUser(Integer id);

    Optional<User> getUser(String email);

    Collection<User> getUsers();

    Collection<User> getUsers(List<Integer> ids);

    Integer getMaxCommonLikesUser(Integer userId);
}
