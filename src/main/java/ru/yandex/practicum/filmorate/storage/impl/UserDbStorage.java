package ru.yandex.practicum.filmorate.storage.impl;

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
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;
    private final FeedMapper feedMapper;

    public UserDbStorage(JdbcTemplate jdbcTemplate,
                         UserMapper userMapper,
                         FeedMapper feedMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
        this.feedMapper = feedMapper;
    }

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
        String sqlQuery = "SELECT * FROM users where id = " + userId + ";";
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
        String sqlQuery = "SELECT id, email, login, name, birthday " +
                "FROM users AS u " +
                "JOIN friends AS f ON  f.followed_user_id = u.id " +
                "WHERE f.following_user_id = " + userId + ";";
        return jdbcTemplate.query(sqlQuery, userMapper);
    }

    @Override
    public boolean deleteUser(int id) {
        String sqlQuery = "delete from users where id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public boolean addFriend(int userId1, int userId2) {
        EventDao.eventAdd(userId2, "FRIEND", "ADD", userId1);
        String sqlQuery = "insert into friends values(?,?)";
        return jdbcTemplate.update(sqlQuery, userId1, userId2) > 0;
    }

    @Override
    public boolean removeFriend(int userId1, int userId2) {
        EventDao.eventAdd(userId2, "FRIEND", "REMOVE", userId1);
        String sqlQuery = "delete from friends where following_user_id = ? AND followed_user_id = ?";
        return jdbcTemplate.update(sqlQuery, userId1, userId2) > 0;
    }

    @Override
    public boolean isValidUser(int id) {
        return jdbcTemplate.queryForRowSet("SELECT id FROM users WHERE id=?", id).next();
    }

    @Override
    public List<Feed> getUserFeed(Integer id) {
        String sqlQuery = "SELECT * FROM FEEDS f WHERE USER_ID = " + id + ";";
        return jdbcTemplate.query(sqlQuery, feedMapper);
    }
}