package ru.yandex.practicum.filmorate.validation;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
abstract class ObjectValidator<T> {
    protected final Errors springValidationErrors;
    @Getter
    protected final T validatedObject;
    @Getter
    protected final List<String> errorMessages;

    protected boolean hasErrors;

    public boolean hasErrors() {
        return hasErrors;
    }

    public ObjectValidator(T object, Errors springValidationErrors) {
        this.validatedObject = object;
        this.springValidationErrors = springValidationErrors;
        errorMessages = new ArrayList<>();
        hasErrors = false;
        checkGlobalSpringValidationErrors();
        checkFieldSpringValidationErrors();
    }

    private void checkGlobalSpringValidationErrors() {
        if (springValidationErrors.hasGlobalErrors()) {
            hasErrors = true;
            errorMessages.addAll(
                    springValidationErrors.getGlobalErrors().stream()
                            .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()));
        }
    }

    private void checkFieldSpringValidationErrors() {
        if (springValidationErrors.hasFieldErrors()) {
            hasErrors = true;
            errorMessages.addAll(
                    springValidationErrors.getFieldErrors().stream()
                            .map(e -> String.format("Поле %s '%s' %s",
                                    e.getField(), e.getRejectedValue(), e.getDefaultMessage()))
                            .collect(Collectors.toList()));
        }
    }
}
