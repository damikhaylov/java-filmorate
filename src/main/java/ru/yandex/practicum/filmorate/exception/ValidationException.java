package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends RuntimeException {
    @Getter
    private final List<String> subErrorsMessages;

    public ValidationException(String message) {
        super(message);
        subErrorsMessages = new ArrayList<>();
    }

    public ValidationException(String message, List<String> subErrorsMessages) {
        super(message);
        this.subErrorsMessages = subErrorsMessages;
    }
}