package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
@RestController
public class UserController {
        private int ID = 0;
        HashMap<Integer, User> users = new HashMap<>();
        @PostMapping("/users")
        public User addUser(@RequestBody User user){
            user.setId(getID());
            users.put(user.getId(),user);
            return user;
        }
        @GetMapping("/users")
        public Set<Map.Entry<Integer,User>> getUsers(){
            return users.entrySet();
        }
        @PutMapping("/users")
        public User updateUser(@RequestBody User user){
            users.put(user.getId(),user);
            return user;
        }
        private int getID() {
            return ID++;
        }

    }

