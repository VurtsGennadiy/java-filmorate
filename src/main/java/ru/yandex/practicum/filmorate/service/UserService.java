package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicateDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendService friendService;

    public User create(User newUser) {
        checkEmailDuplicate(newUser.getEmail());
        checkUserName(newUser);
        User user = userStorage.add(newUser);
        friendService.addUser(user.getId());
        return user;
    }

    public User update(User user) {
        User oldEntry = userStorage.getUser(user.getId());
        if (!oldEntry.getEmail().equals(user.getEmail())) {
            checkEmailDuplicate(user.getEmail());
        }
        checkUserName(user);
        return userStorage.update(user);
    }

    public User remove(Integer id) {
        User user = userStorage.remove(id);
        friendService.getFriends(id).stream()
                .map(User::getId)
                .forEach((Integer friendId) -> friendService.removeFriend(friendId, id));
        return user;
    }

    public User getUser(Integer id) {
        return userStorage.getUser(id);
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
        if (userStorage.getUsers().stream().map(User::getEmail).anyMatch(Predicate.isEqual(email))) {
            throw new DuplicateDataException("Пользователь с таким email уже существует.");
        }
    }
}
