package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private int nextId = 1;

    @Override
    public User create(User user) {
        user.setId(nextId);
        users.put(user.getId(), user);
        nextId++;
        return user;
    }

    @Override
    public User update(User user) {
        users.replace(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    @Override
    public User get(long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean contains(long id) {
        return users.containsKey(id);
    }
}
