package ru.yandex.practicum.filmorate.storage;

public interface EventStorage {

    void eventAdd(int entityId, String eventType, String operation, int userId);
}
