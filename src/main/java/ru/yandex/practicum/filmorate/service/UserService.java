package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public boolean addFriend(int userId1, int userId2) {
        if (userStorage.getUserById(userId1) == null || userStorage.getUserById(userId2) == null) {
            throw new IncorrectIdException("Incorrect user Id");
        } else if (userId1 == userId2) {
            throw new IncorrectIdException("You cannot add yourself to friends!");
        } else {
            userStorage.getUserById(userId1).getFriends().add(userId2);
            return userStorage.getUserById(userId2).getFriends().add(userId1);
        }
    }

    public boolean removeFriend(int userId1, int userId2) {
        if (userStorage.getUserById(userId1) == null || userStorage.getUserById(userId2) == null) {
            throw new IncorrectIdException("Incorrect user Id");
        } else if (userId1 == userId2) {
            throw new IncorrectIdException("You cannot add or remove yourself from friends!");
        } else {

            return userStorage.getUserById(userId2).getFriends().remove(userId1) &&
                    userStorage.getUserById(userId1).getFriends().remove(userId2);
        }
    }

    public List<User> returnCommonFriends(int userId1, int userId2) {
        if (userStorage.getUserById(userId1) == null || userStorage.getUserById(userId2) == null) {
            throw new IdNotFoundException("Incorrect user Id");
        } else if (userId1 == userId2) {
            throw new IncorrectIdException("Ids cannot be same!");
        } else {
            return userStorage.getUserById(userId1).getFriends().stream()
                    .filter(id -> userStorage.getUserById(userId2).getFriends().contains(id))
                    .map(id -> userStorage.getUserById(id))
                    .collect(Collectors.toList());
        }
    }

    public boolean isValidUser(int id) {
        return userStorage.getUserById(id) != null;
    }
}



