package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private UserValidator validator;

    @PostMapping()
    public User add(@Valid @RequestBody User user, Errors springValidationErrors) {
        validator = new UserValidator(user, springValidationErrors);
        if (validator.hasErrors()) {
            throw new ValidationException("Данные пользователя содержат ошибки и не были добавлены.",
                    validator.getErrorMessages());
        }
        User savedUser = userService.add(validator.getValidatedObject());
        log.info("Добавлен пользователь: {}", savedUser);
        return savedUser;
    }

    @PutMapping()
    public User update(@Valid @RequestBody User user, Errors springValidationErrors) {
        validator = new UserValidator(user, springValidationErrors);
        if (validator.hasErrors()) {
            throw new ValidationException("Данные пользователя содержат ошибки не были обновлены.",
                    validator.getErrorMessages());
        }
        User updatedUser = userService.update(validator.getValidatedObject());
        log.info("Данные пользователя обновлены: {}", updatedUser);
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable long id) {
        userService.remove(id);
        log.info("Удалён пользователь id={}", id);
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable long id) {
        return userService.get(id);
    }

    @GetMapping()
    public List<User> getAll() {
        return userService.getAll();
    }


    @PutMapping("/{id}/friends/{friendId}")
    public List<User> addFriend(@PathVariable long id, @PathVariable long friendId) {
        List<User> friends = userService.addFriend(id, friendId);
        log.info("Пользователи '{}'[id={}] и '{}'[id={}] добавлены друг к другу в друзья",
                friends.get(0).getName(), friends.get(0).getId(),
                friends.get(1).getName(), friends.get(1).getId());
        return friends;
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void removeFromFriends(@PathVariable long id, @PathVariable long friendId) {
        userService.removeFromFriends(id, friendId);
        log.info("Пользователи [id={}] и [id={}] удалены из списка друзей друг у друга", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable long id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}