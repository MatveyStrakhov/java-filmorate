package ru.yandex.practicum.filmorate.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.IncorrectIdException;
import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class ExceptionsHandler {
    @Data
    @Builder
    private static class ErrorJson {
        int status;
        String error;
        LocalDateTime timestamp;
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Validation failed:" + e.getMessage());
        ErrorJson error = ErrorJson.builder()
                .error("Validation failed: " + e.getFieldError().getDefaultMessage())
                .timestamp(LocalDateTime.now())
                .status(400)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = IncorrectIdException.class)
    ResponseEntity<Object> handleIncorrectIdException(IncorrectIdException e) throws JsonProcessingException {
        ErrorJson error = ErrorJson.builder()
                .error("ID not found")
                .timestamp(LocalDateTime.now())
                .status(404)
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
