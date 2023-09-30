package ru.yandex.practicum.filmorate.storage;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.model.User;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();
    private int iD = 1;

    @Override
    public User createUser(User user) {
        user.setId(getID());
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("User added:" + user.toString());
        return user;
    }

    @Override
    public Collection<User> returnAllUsers() {
        return users.values();
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new IncorrectIdException("Validation failed: wrong id");
        } else {
            if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("User updated:" + user.toString());
            return user;
        }
    }

    @Override

    public User getUserById(int userId) {
        return users.getOrDefault(userId, null);
    }

    @Override
    public Collection<User> getFriendsList(int id) {
        if (getUserById(id) == null) {
            throw new IdNotFoundException("This ID doesn't exist!");
        } else {
            return getUserById(id).getFriends().stream()
                    .map(this::getUserById)
                    .collect(Collectors.toList());
        }
    }

    private int getID() {
        return iD++;
    }
}
