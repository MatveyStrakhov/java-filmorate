package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    User createUser(User user);

    Collection<User> returnAllUsers();

    User updateUser(User user);

    User getUserById(int userId);

    Collection<User> getFriendsList(int id);

    void deleteUser(int id);

    void addFriend(int userId1, int userId2);

    void removeFriend(int userId1, int userId2);

    boolean isValidUser(int id);

    List<Feed> getUserFeed(Integer id);
}
