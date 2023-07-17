package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class UserController {
    private int iD = 1;
    private Map<Integer, User> users = new HashMap<>();

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
        user.setId(getID());
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("User added:" + user.toString());
        return user;
    }

    @GetMapping("/users")
    public Collection<User> getUsers() {
        return users.values();
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Validation failed: wrong id");
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


    private int getID() {
        return iD++;
    }

    @ExceptionHandler
    void handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Validation failed:" + e.getMessage());
        throw new ValidationException("Validation failed:" + e.getMessage());
    }


}

