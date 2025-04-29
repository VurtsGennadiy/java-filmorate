package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface FriendStorage {
    void add(Integer firstId, Integer secondId);

    void remove(Integer firstId, Integer secondId);

    List<Integer> get(Integer id);
}
