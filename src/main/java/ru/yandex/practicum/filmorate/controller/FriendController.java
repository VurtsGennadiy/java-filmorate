package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendService;

import java.util.Collection;

@RestController
@RequestMapping("/users/{id}/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @PutMapping("/{friendId}")
    public void addFriend(@PathVariable Integer id,
                          @PathVariable Integer friendId) {
        friendService.addFriend(id, friendId);
    }

    @DeleteMapping("/{friendId}")
    public void removeFriend(@PathVariable Integer id,
                             @PathVariable Integer friendId) {
        friendService.removeFriend(id, friendId);
    }

    @GetMapping()
    public Collection<User> get(@PathVariable Integer id) {
        return friendService.getFriends(id);
    }

    @GetMapping("/common/{otherId}")
    public Collection<User> getCommon(@PathVariable Integer id,
                                      @PathVariable Integer otherId) {
        return friendService.getCommon(id, otherId);
    }
}
