package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedMapper;
import ru.yandex.practicum.filmorate.storage.UserMapper;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Component("dbStorage")
@Primary
@Slf4j
@AllArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;
    private final FeedMapper feedMapper;
    private final EventDao eventDao;

    @Override
    public User createUser(User user) {
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue());
        return user;
    }

    @Override
    public Collection<User> returnAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(
                sql, userMapper);
        return users;
    }

    @Override
    public User updateUser(User user) {
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        String sqlQuery = "update users set " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "where id = ?";
        if (jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()) > 0) {
            log.info("User updated: " + user.getId());
            return user;
        } else {
            log.error("User update failure");
            throw new IdNotFoundException("User update failure");
        }
    }

    @Override
    public User getUserById(int userId) {
        String sqlQuery = "SELECT * FROM users WHERE id = " + userId + ";";
        try {
            User user = jdbcTemplate.queryForObject(sqlQuery, userMapper);
            log.info("User found: {} {}", user.getId(), user.getLogin());
            return user;
        } catch (DataAccessException e) {
            log.info("User not found: " + userId);
            throw new IdNotFoundException("User not found:" + userId);
        }
    }


    @Override
    public Collection<User> getFriendsList(int userId) {
        if (isValidUser(userId)) {
            String sqlQuery = "SELECT id, email, login, name, birthday " +
                    "FROM users AS u " +
                    "JOIN friends AS f ON f.followed_user_id = u.id " +
                    "WHERE f.following_user_id = ?";
            return jdbcTemplate.query(sqlQuery, userMapper, userId);
        } else {
            throw new IdNotFoundException("User not found:" + userId);
        }
    }

    @Override
    public User deleteUser(int id) {
        if (isValidUser(id)) {
            String sqlForFeeds = "DELETE FROM feeds WHERE user_id IN (SELECT id FROM users WHERE id = ?)";
            jdbcTemplate.update(sqlForFeeds, id);
            String sqlForLikes = "DELETE FROM likes WHERE user_id IN (SELECT id FROM users WHERE id = ?)";
            jdbcTemplate.update(sqlForLikes, id);
            String sqlForFriends = "DELETE FROM friends WHERE following_user_id = ? OR followed_user_id = ?";
            jdbcTemplate.update(sqlForFriends, id, id);
            String sqlForFilms = "DELETE FROM users WHERE id = ?";
            jdbcTemplate.update(sqlForFilms, id);
            return null;
        } else {
            throw new IdNotFoundException("User not found!");
        }
    }

    @Override
    public boolean addFriend(int userId1, int userId2) {
        eventDao.eventAdd(userId2, "FRIEND", "ADD", userId1);
        String sqlQuery = "INSERT INTO friends VALUES(?,?)";
        return jdbcTemplate.update(sqlQuery, userId1, userId2) > 0;
    }

    @Override
    public boolean removeFriend(int userId1, int userId2) {
        eventDao.eventAdd(userId2, "FRIEND", "REMOVE", userId1);
        String sqlQuery = "DELETE FROM friends WHERE following_user_id = ? AND followed_user_id = ?";
        return jdbcTemplate.update(sqlQuery, userId1, userId2) > 0;
    }

    @Override
    public boolean isValidUser(int id) {
        return jdbcTemplate.queryForRowSet("SELECT id FROM users WHERE id = ?", id).next();
    }

    @Override
    public List<Feed> getUserFeed(Integer id) {
        String sqlQuery = "SELECT * FROM feeds f WHERE user_id = ?;";
        return jdbcTemplate.query(sqlQuery, feedMapper, id);
    }
}