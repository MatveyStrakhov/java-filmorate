package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@RestController
public class UserController {
    private int iD = 1;
    HashMap<Integer, User> users = new HashMap<>();

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Validation failed: login must not contain blanks");
            throw new ValidationException("Validation failed: login must not contain blanks");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Validation failed: unbirthed people arent allowed");
            throw new ValidationException("Validation failed: unbirthed people arent allowed");
        } else {
            user.setId(getID());
            if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("User added:" + user.toString());
            return user;
        }
    }

    @GetMapping("/users")
    public Collection<User> getUsers() {
        return users.values();
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Validation failed: login must not contain blanks");
            throw new ValidationException("Validation failed: login must not contain blanks");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Validation failed: unbirthed people arent allowed");
            throw new ValidationException("Validation failed: unbirthed people arent allowed");
        } else if (!users.containsKey(user.getId())) {
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

}

