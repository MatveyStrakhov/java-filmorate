package ru.yandex.practicum.filmorate.storage;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;


import java.util.HashMap;
import java.util.Map;


@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();
    private int iD = 1;


    @Override
    public int getID() {
        return iD++;
    }

    @Override
    public Map<Integer, User> getUsers() {
        return users;
    }
}
