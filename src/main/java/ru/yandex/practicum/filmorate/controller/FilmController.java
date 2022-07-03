package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.FilmorateValidator;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    @PostMapping()
    public Film create(@Valid @RequestBody Film film) {
        film.setId(nextId);
        if (!FilmorateValidator.validateFilm(film)) {
            log.warn("Ошибка валидации данных: Данные фильма {} не были добавлены", film);
            throw new ValidationException("Данные фильма содержат ошибки и не были добавлены.");
        }
        films.put(film.getId(), film);
        nextId++;
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping()
    public Film update(@Valid @RequestBody Film film) {
        checkExistenceById(film.getId());
        if (!FilmorateValidator.validateFilm(film)) {
            log.warn("Ошибка валидации данных: Данные фильма {} не были обновлены", film);
            throw new ValidationException("Данные фильма содержат ошибки и не были обновлены.");
        }
        films.put(film.getId(), film);
        log.info("Данные фильма обновлены: {}", film);
        return film;
    }

    @GetMapping()
    public Collection<Film> findAll() {
        return films.values();
    }

    private void checkExistenceById(int id) throws NonExistentIdException {
        if (!films.containsKey(id)) {
            log.warn("Данные фильма не были обновлены, фильм с укзанным id={} не существует", id);
            throw new NonExistentIdException(String.format("Фильм с id=%d не существует.", id));
        }
    }
}