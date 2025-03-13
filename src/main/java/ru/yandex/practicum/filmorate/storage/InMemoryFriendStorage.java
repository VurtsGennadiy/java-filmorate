package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryFriendStorage implements FriendStorage {
    private final Map<Integer, Set<Integer>> friends = new HashMap<>();

    @Override
    public void add(Integer firstId, Integer secondId) {
        log.trace("Добавить дружбу между пользователями id = {} и id = {}", firstId, secondId);
        friends.get(firstId).add(secondId);
        friends.get(secondId).add(firstId);
    }

    @Override
    public void remove(Integer firstId, Integer secondId) {
        log.trace("Удалить дружбу между пользователями id = {} и id = {}", firstId, secondId);
        friends.get(firstId).remove(secondId);
        friends.get(secondId).remove(firstId);
    }

    @Override
    public void addEmpty(Integer userId) {
        log.trace("Добавить пустую запись в хранилище друзей для пользователя id = {}", userId);
        friends.put(userId, new HashSet<>());
    }

    @Override
    public Set<Integer> get(Integer id) {
        return friends.get(id);
    }
}
