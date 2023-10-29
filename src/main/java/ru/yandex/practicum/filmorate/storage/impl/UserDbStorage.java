package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmsExtractor;
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

    public UserDbStorage(JdbcTemplate jdbcTemplate, UserMapper userMapper, FilmsExtractor filmsExtractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
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
        List<User> users = jdbcTemplate.query(sql, userMapper);
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
            log.info("User updated: {}", user.getId());
            return user;
        } else {
            log.error("User update failure");
            throw new IdNotFoundException("User update failure");
        }
    }

    @Override
    public User getUserById(int userId) {
        String sqlQuery = "SELECT * FROM users where id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sqlQuery, userMapper, userId);
            log.info("User found: {} {}", user.getId(), user.getLogin());
            return user;
        } catch (DataAccessException e) {
            log.info("User not found: {}", userId);
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
            String sqlForLikes = "delete from likes where user_id in (select id from users where id = ?)";
            jdbcTemplate.update(sqlForLikes, id);
            String sqlForFriends = "delete from friends where following_user_id = ? or followed_user_id = ?";
            jdbcTemplate.update(sqlForFriends, id, id);
            String sqlForFilms = "delete from users where id = ?";
            jdbcTemplate.update(sqlForFilms, id);
            return null;
        } else {
            throw new IdNotFoundException("User not found!");
        }
    }

    @Override
    public boolean addFriend(int userId1, int userId2) {
        String sqlQuery = "insert into friends values(?,?)";
        return jdbcTemplate.update(sqlQuery, userId1, userId2) > 0;
    }

    @Override
    public boolean removeFriend(int userId1, int userId2) {
        String sqlQuery = "delete from friends where following_user_id = ? AND followed_user_id = ?";
        return jdbcTemplate.update(sqlQuery, userId1, userId2) > 0;
    }

    @Override
    public boolean isValidUser(int id) {
        return jdbcTemplate.queryForRowSet("SELECT id FROM users WHERE id=?", id).next();
    }
}