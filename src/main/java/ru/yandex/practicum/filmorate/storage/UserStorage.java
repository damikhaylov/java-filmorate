package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User create(User user);

    User update(User user);

    void delete(long id);

    User get(long id);

    List<User> getAll();

    boolean contains(long id);
}
