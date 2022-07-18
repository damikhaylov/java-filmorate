package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NonExistentIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<?> handleNonExistentIdException(final NonExistentIdException exception) {
        log.warn("Ошибка (объект не найден): {}", exception.getMessage());
        return new ResponseEntity<>(
                Map.of("Ошибка (объект не найден)", exception.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    public ResponseEntity<?> handleValidationException(final ValidationException exception) {
        exception.getSubErrorsMessages().forEach(s -> log.warn("Ошибка валидации: {}", s));
        log.warn("Ошибки валидации: {}", exception.getMessage());
        return new ResponseEntity<>(
                Map.of("Ошибки валидации", exception.getSubErrorsMessages()),
                HttpStatus.BAD_REQUEST
        );
    }
}
