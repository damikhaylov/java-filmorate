package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NonExistentIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film add(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        Film oldFilm = get(film.getId());
        film.getLikes().addAll(oldFilm.getLikes());
        return filmStorage.update(film);
    }

    public void remove(long id) {
        if (!filmStorage.contains(id)) {
            throw new NonExistentIdException("Не существует фильма id=" + id);
        }
        filmStorage.delete(id);
    }

    public Film get(long id) {
        final Film film = filmStorage.get(id);
        if (null == film) {
            throw new NonExistentIdException("Не существует фильма id=" + id);
        }
        return film;
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public void addLike(long filmId, long userId) {
        Film film = get(filmId);
        if (!userStorage.contains(userId)) {
            throw new NonExistentIdException("Не существует пользователя id=" + userId);
        }
        film.getLikes().add(userId);
    }

    public void removeLike(long filmId, long userId) {
        Film film = get(filmId);
        if (!film.getLikes().contains(userId)) {
            throw new NonExistentIdException(
                    String.format("У фильма [id=%d] нет обнаружено лайков от пользователя [id=%d]", filmId, userId));
        }
        film.getLikes().remove(userId);
    }

    public List<Film> getPopulars(long count) {
        return filmStorage.getAll()
                .stream()
                .sorted(Comparator.comparing(x -> x.getLikes().size(), Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
