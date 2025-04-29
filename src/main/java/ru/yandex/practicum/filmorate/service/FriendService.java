package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public void addFriend(Integer userId, Integer friendId) {
        checkUserExists(userId);
        checkUserExists(friendId);
        friendStorage.add(userId, friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        checkUserExists(userId);
        checkUserExists(friendId);
        friendStorage.remove(userId, friendId);
    }

    public Collection<User> getFriends(Integer userId) {
        checkUserExists(userId);
        List<Integer> friendsIds = friendStorage.get(userId);
        return userStorage.getUsers(friendsIds);
    }

    public Collection<User> getCommon(Integer firstId, Integer secondId) {
        checkUserExists(firstId);
        checkUserExists(secondId);
        Collection<User> firstUserFriends = getFriends(firstId);
        Collection<User> secondUserFriends = getFriends(secondId);
        firstUserFriends.retainAll(secondUserFriends);
        return firstUserFriends;
    }

    private void checkUserExists(Integer userId) {
        userStorage.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id = " + userId + " не существует"));
    }
}
