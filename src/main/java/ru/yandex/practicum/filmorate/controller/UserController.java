package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping()
    public User addUser(@Valid @RequestBody User user) {
        log.info("Получен запрос POST /users — добавление пользователя");
        return userService.createUser(user);
    }

    @GetMapping()
    public Collection<User> getAllUsers() {
        log.info("Получен запрос GET /users — получение пользователей");
        return userService.returnAllUsers();
    }

    @GetMapping("/{id}")
    public User getUsers(@PathVariable Integer id) {
        log.info("Получен запрос GET /users/{Id} — получение пользователя по id");
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/feed")
    public List<Feed> getUserFeed(@PathVariable Integer id) {
        log.info("Получен запрос GET /users/{id}/feed — получение рекомендайций пользователя");
        return userService.getUserFeed(id);
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос PUT /users — обновление пользователя");
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addToFriends(@PathVariable int id, @PathVariable int friendId) {
        log.info("Получен запрос PUT /users/{id}/friends/{friendId} — обновление данных по друзьям");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFromFriends(@PathVariable int id, @PathVariable int friendId) {
        log.info("Получен запрос DELETE /users/{id}/friends/{friendId} — удаление данных по друзьям");
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriendsOfUser(@PathVariable int id) {
        log.info("Получен запрос GET /users/{id}/friends — получение списка друзей");
        return userService.getFriendsList(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriendsOfTwoUsers(@PathVariable int id, @PathVariable int otherId) {
        log.info("Получен запрос GET /users/{id}/friends/{friendId} — получение списка общих друзей");
        return userService.returnCommonFriends(id, otherId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        log.info("Получен запрос DELETE /users/{userId} — удаление пользователя");
        userService.deleteUser(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable int id) {
        log.info("Получен запрос GET /users/{id}/recommendations — получение списка рекомендаций");
        return userService.getRecommendedFilms(id);
    }
}

