package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NonExistentIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public User add(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        User oldUser = get(user.getId());
        user.getFriends().addAll(oldUser.getFriends());
        return userStorage.update(user);
    }

    public void remove(long id) {
        User user = get(id);
        user.getFriends().forEach(x -> get(x).getFriends().remove(id));
        userStorage.delete(id);
    }

    public User get(long id) {
        final User user = userStorage.get(id);
        if (null == user) {
            throw new NonExistentIdException("Не существует пользователя id=" + id);
        }
        return user;
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public List<User> addFriend(long user1Id, long user2Id) {
        User user1 = get(user1Id);
        User user2 = get(user2Id);
        user1.getFriends().add(user2Id);
        user2.getFriends().add(user1Id);
        return List.of(user1, user2);
    }

    public List<User> getUserFriends(long id) {
        return get(id).getFriends().stream().map(this::get).collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long user1Id, long user2Id) {
        User user1 = get(user1Id);
        User user2 = get(user2Id);
        Set<Long> commonFriendsIds = new HashSet<>(user1.getFriends());
        commonFriendsIds.retainAll(user2.getFriends());
        return commonFriendsIds.stream().map(this::get).collect(Collectors.toList());
    }

    public void removeFromFriends(long user1Id, long user2Id) {
        User user1 = get(user1Id);
        User user2 = get(user2Id);
        user1.getFriends().remove(user2Id);
        user2.getFriends().remove(user1Id);
    }
}
