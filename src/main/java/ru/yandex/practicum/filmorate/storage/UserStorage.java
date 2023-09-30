package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public User createUser(User user);

    Collection<User> returnAllUsers();

    User updateUser(User user);

    User getUserById(int userId);

    Collection<User> getFriendsList(int id);
}
