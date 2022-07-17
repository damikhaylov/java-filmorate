package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private int nextId = 1;

    @Override
    public Film create(Film film) {
        film.setId(nextId);
        films.put(film.getId(), film);
        nextId++;
        return film;
    }

    @Override
    public Film update(Film film) {
        films.replace(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public void delete(long id) {
        films.remove(id);
    }

    @Override
    public Film get(long id) {
        return films.get(id);
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public boolean contains(long id) {
        return films.containsKey(id);
    }
}
