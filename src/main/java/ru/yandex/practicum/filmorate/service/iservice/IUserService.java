package ru.yandex.practicum.filmorate.service.iservice;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface IUserService {

    boolean addFriend(int userId1, int userId2);

    boolean removeFriend(int userId1, int userId2);

    List<User> returnCommonFriends(int userId1, int userId2);

    boolean isValidUser(int id);

    User createUser(User user);

    Collection<User> returnAllUsers();

    User updateUser(User user);

    User getUserById(int userId);

    Collection<User> getFriendsList(int id);

    void deleteUser(int id);

    List<Feed> getUserFeed(Integer id);

    List<Film> getRecommendedFilms(int userId);
}
