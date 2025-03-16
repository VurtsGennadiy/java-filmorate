package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User add(User user);

    User remove(Integer id);

    User update(User user);

    User getUser(Integer id);

    Collection<User> getUsers();
}
