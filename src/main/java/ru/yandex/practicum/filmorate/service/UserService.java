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
        return userStorage.createUser(user);
    }


    public Collection<User> returnAllUsers() {
        return userStorage.returnAllUsers();
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }


    public User getUserById(int userId) {
        return userStorage.getUserById(userId);
    }


    public Collection<User> getFriendsList(int id) {
        return userStorage.getFriendsList(id);
    }
}



