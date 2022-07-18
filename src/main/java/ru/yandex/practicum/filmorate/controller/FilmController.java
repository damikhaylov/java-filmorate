package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;
    private FilmValidator validator;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping()
    public Film add(@Valid @RequestBody Film film, Errors springValidationErrors) {
        validator = new FilmValidator(film, springValidationErrors);
        if (validator.hasErrors()) {
            throw new ValidationException("Данные фильма содержат ошибки и не были добавлены.",
                    validator.getErrorMessages());
        }
        Film savedFilm = filmService.add(validator.getValidatedObject());
        log.info("Добавлен фильм: {}", savedFilm);
        return savedFilm;
    }

    @PutMapping()
    public Film update(@Valid @RequestBody Film film, Errors springValidationErrors) {
        validator = new FilmValidator(film, springValidationErrors);
        if (validator.hasErrors()) {
            throw new ValidationException("Данные фильма содержат ошибки не были обновлены.",
                    validator.getErrorMessages());
        }
        Film updatedFilm = filmService.update(validator.getValidatedObject());
        log.info("Данные фильма обновлены: {}", updatedFilm);
        return updatedFilm;
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable long id) {
        filmService.remove(id);
        log.info("Удалён фильм id={}", id);
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable long id) {
        return filmService.get(id);
    }

    @GetMapping()
    public List<Film> getAll() {
        return filmService.getAll();
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
        log.info("Фильмом [id={}] получен лайк от пользователя [id={}]", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        filmService.removeLike(id, userId);
        log.info("У фильма [id={}] удалён лайк от пользователя [id={}]", id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopulars(@RequestParam(defaultValue = "10") long count) {
        return filmService.getPopulars(count);
    }
}