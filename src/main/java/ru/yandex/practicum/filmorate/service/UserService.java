package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public boolean addFriend(int userId1, int userId2) {
        if (getUserById(userId1) == null || getUserById(userId2) == null) {
            throw new IncorrectIdException("Incorrect user Id");
        } else if (userId1 == userId2) {
            throw new IncorrectIdException("You cannot add yourself to friends!");
        } else {
            getUserById(userId1).getFriends().add(userId2);
            return getUserById(userId2).getFriends().add(userId1);
        }
    }

    public boolean removeFriend(int userId1, int userId2) {
        if (getUserById(userId1) == null || getUserById(userId2) == null) {
            throw new IncorrectIdException("Incorrect user Id");
        } else if (userId1 == userId2) {
            throw new IncorrectIdException("You cannot add or remove yourself from friends!");
        } else {

            return getUserById(userId2).getFriends().remove(userId1) &&
                    getUserById(userId1).getFriends().remove(userId2);
        }
    }

    public List<User> returnCommonFriends(int userId1, int userId2) {
        if (getUserById(userId1) == null || getUserById(userId2) == null) {
            throw new IdNotFoundException("Incorrect user Id");
        } else if (userId1 == userId2) {
            throw new IncorrectIdException("Ids cannot be same!");
        } else {
            return getUserById(userId1).getFriends().stream()
                    .filter(id -> getUserById(userId2).getFriends().contains(id))
                    .map(this::getUserById)
                    .collect(Collectors.toList());
        }
    }

    public boolean isValidUser(int id) {
        return getUserById(id) != null;
    }

    public User createUser(User user) {
        user.setId(userStorage.getID());
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userStorage.getUsers().put(user.getId(), user);
        log.info("User added:" + user.toString());
        return user;
    }


    public Collection<User> returnAllUsers() {
        return userStorage.getUsers().values();
    }

    public User updateUser(User user) {
        if (!userStorage.getUsers().containsKey(user.getId())) {
            throw new IncorrectIdException("Validation failed: wrong id");
        } else {
            if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            userStorage.getUsers().put(user.getId(), user);
            log.info("User updated:" + user.toString());
            return user;
        }
    }


    public User getUserById(int userId) {
        return userStorage.getUsers().getOrDefault(userId, null);
    }


    public Collection<User> getFriendsList(int id) {
        if (getUserById(id) == null) {
            throw new IdNotFoundException("This ID doesn't exist!");
        } else {
            return getUserById(id).getFriends().stream()
                    .map(this::getUserById)
                    .collect(Collectors.toList());
        }
    }
}



