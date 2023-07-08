package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
@Slf4j
@RestController
public class UserController {
        private int ID = 0;
        HashMap<Integer, User> users = new HashMap<>();
        @PostMapping("/users")
        public User addUser(@Valid @RequestBody User user){
            if(user.getLogin().contains(" ")||user.getBirthday().isAfter(LocalDate.now())){
                log.error("Validation failed");
                throw new ValidationException();
            }
            else{
            user.setId(getID());
            if (user.getName().isEmpty()||user.getName().isBlank()){
                user.setName(user.getLogin());
            }
            users.put(user.getId(),user);
            return user;}
        }
        @GetMapping("/users")
        public Set<Map.Entry<Integer,User>> getUsers(){
            return users.entrySet();
        }
        @PutMapping("/users")
        public User updateUser(@Valid @RequestBody User user){
            if(user.getLogin().contains(" ")||user.getBirthday().isAfter(LocalDate.now())){
                log.error("Validation failed");
                throw new ValidationException();
            }
            else{
                if (user.getName().isEmpty()||user.getName().isBlank()){
                    user.setName(user.getLogin());
                }
                users.put(user.getId(),user);
                return user;}
        }
        private int getID() {
            return ID++;
        }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private void exceptionHandler(){
        log.error("Validation failed");
            throw new ValidationException();
    }

    }

