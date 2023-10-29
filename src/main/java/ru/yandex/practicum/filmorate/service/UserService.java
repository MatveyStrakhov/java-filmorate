package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.Feed;
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
        if (!isValidUser(userId1) || !isValidUser(userId2)) {
            throw new IdNotFoundException("User with this id does not exist!");
        } else if (userId1 == userId2) {
            throw new IncorrectIdException("You cannot add yourself to friends!");
        } else {
            return userStorage.addFriend(userId1, userId2);
        }
    }

    public boolean removeFriend(int userId1, int userId2) {
        if (!isValidUser(userId1) || !isValidUser(userId2)) {
            throw new IdNotFoundException("User with this id does not exist!");
        } else if (userId1 == userId2) {
            throw new IncorrectIdException("You cannot add or remove yourself from friends!");
        } else {
            return userStorage.removeFriend(userId1, userId2);
        }
    }

    public List<User> returnCommonFriends(int userId1, int userId2) {
        if (!isValidUser(userId1) || !isValidUser(userId2)) {
            throw new IdNotFoundException("Incorrect user Id");
        } else if (userId1 == userId2) {
            throw new IncorrectIdException("Ids cannot be same!");
        } else {
            return userStorage.getFriendsList(userId1).stream()
                    .filter(user -> userStorage.getFriendsList(userId2).contains(user))
                    .collect(Collectors.toList());
        }
    }

    public boolean isValidUser(int id) {
        return userStorage.isValidUser(id);
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }


    public Collection<User> returnAllUsers() {
        return userStorage.returnAllUsers();
    }

    public User updateUser(User user) {
        if (isValidUser(user.getId())) {
            return userStorage.updateUser(user);
        } else {
            throw new IdNotFoundException("User with this id does not exist!");
        }
    }

    public User getUserById(int userId) {
        return userStorage.getUserById(userId);
    }


    public Collection<User> getFriendsList(int id) {
        return userStorage.getFriendsList(id);
    }

    public User deleteUser(int id) {
        if (isValidUser(id)) {
            return userStorage.deleteUser(id);
        } else {
            throw new IdNotFoundException("User with this id does not exist!");
        }
    }

    public List<Feed> getUserFeed(Integer id) {
        return userStorage.getUserFeed(id);
    }
}



