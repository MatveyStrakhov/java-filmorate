package ru.yandex.practicum.filmorate.exception;

public class NotFoundExeption extends RuntimeException {
    public NotFoundExeption(String message) {
        super(message);
    }
}