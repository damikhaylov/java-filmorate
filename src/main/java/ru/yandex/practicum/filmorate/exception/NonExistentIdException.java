package ru.yandex.practicum.filmorate.exception;

public class NonExistentIdException extends RuntimeException {
    public NonExistentIdException(String message) {
        super(message);
    }
}