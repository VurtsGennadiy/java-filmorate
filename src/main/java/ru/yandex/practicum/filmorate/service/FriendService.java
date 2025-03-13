package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public void addUser(Integer id) {
        friendStorage.addEmpty(id);
    }

    public void addFriend(Integer userId, Integer friendId) {
        userStorage.getUser(userId);
        userStorage.getUser(friendId);
        friendStorage.add(userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        userStorage.getUser(userId);
        userStorage.getUser(friendId);
        friendStorage.remove(userId, friendId);
    }

    public Collection<User> getFriends(Integer userId) {
        userStorage.getUser(userId);
        return friendStorage.get(userId).stream()
                .map(userStorage::getUser)
                .toList();
    }

    public Collection<User> getCommon(Integer firstId, Integer secondId) {
        Collection<User> firstUserFriends = getFriends(firstId);
        Collection<User> secondUserFriends = getFriends(secondId);
        return firstUserFriends.stream().filter(secondUserFriends::contains).toList();
    }
}
