package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.FilmorateValidator;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private int nextId = 1;

    @PostMapping()
    public User create(@Valid @RequestBody User user, Errors springValidationErrors) {
        user.setId(nextId);
        if (!FilmorateValidator.validateUser(user, springValidationErrors)) {
            log.warn("Ошибка валидации данных: Данные пользователя {} не были добавлены", user);
            throw new ValidationException("Данные пользователя содержат ошибки и не были добавлены.");
        }
        users.put(user.getId(), user);
        nextId++;
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping()
    public User update(@Valid @RequestBody User user, Errors springValidationErrors) {
        checkExistenceById(user.getId());
        if (!FilmorateValidator.validateUser(user, springValidationErrors)) {
            log.warn("Ошибка валидации данных: Данные пользователя {} не были обновлены", user);
            throw new ValidationException("Данные пользователя содержат ошибки и не были обновлены.");
        }
        users.put(user.getId(), user);
        log.info("Данные пользователя обновлены: {}", user);
        return user;
    }

    @GetMapping()
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    private void checkExistenceById(long id) throws NonExistentIdException {
        if (!users.containsKey(id)) {
            log.warn("Данные пользователя не были обновлены, пользователь с указанным id={} не существует", id);
            throw new NonExistentIdException(String.format("Пользователь с id=%d не существует.", id));
        }
    }
}