package ru.yandex.practicum.filmorate.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;

@Slf4j
@ControllerAdvice
public class ExceptionsHandler {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    void handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn(e.getMessage());
    }

    @ExceptionHandler(value = IncorrectIdException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
   void handleIncorrectIdException(IncorrectIdException e) {
        log.warn(e.getMessage());
    }
}
