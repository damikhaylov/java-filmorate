package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NonExistentIdException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNonExistentIdException(final NonExistentIdException exception) {
        log.warn("Ошибка (объект не найден): {}", exception.getMessage());
        return Map.of("Ошибка (объект не найден)", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<String>> handleValidationException(final ValidationException exception) {
        exception.getSubErrorsMessages().forEach(s -> log.warn("Ошибка валидации: {}", s));
        log.warn("Ошибки валидации: {}", exception.getMessage());
        return Map.of("Ошибки валидации", exception.getSubErrorsMessages());
    }
}
