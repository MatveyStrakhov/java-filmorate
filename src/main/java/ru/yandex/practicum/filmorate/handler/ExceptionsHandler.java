package ru.yandex.practicum.filmorate.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;

@Slf4j
@ControllerAdvice
public class ExceptionsHandler {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Validation failed:" + e.getMessage());
        return new ResponseEntity<>("Validation failed: " + e.getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = IncorrectIdException.class)
    ResponseEntity<Object> handleIncorrectIdException(IncorrectIdException e) {
        return new ResponseEntity<>("ID not found", HttpStatus.NOT_FOUND);
    }
}
