package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
        return userStorage.createUser(user);
    }

    @GetMapping(value = {"/users", "/users/{id}"})
    public Object getUsers(@PathVariable(required = false) Integer id) {
        if (id == null) {
            return userStorage.returnAllUsers();
        } else {
            if (userStorage.getUserById(id) != null) {
                return userStorage.getUserById(id);
            } else {
                throw new IdNotFoundException("This ID doesn't exist!");
            }
        }

    }


    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        return userStorage.updateUser(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addToFriends(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void removeFromFriends(@PathVariable int id, @PathVariable int friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> getFriendsOfUser(@PathVariable int id) {
        return userStorage.getFriendsList(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriendsOfTwoUsers(@PathVariable int id, @PathVariable int otherId) {
        return userService.returnCommonFriends(id, otherId);
    }


}

