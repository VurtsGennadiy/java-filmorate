package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

public interface FriendStorage {
    void add(Integer firstId, Integer secondId);

    void addEmpty(Integer userId);

    void remove(Integer firstId, Integer secondId);

    Set<Integer> get(Integer id);
}
