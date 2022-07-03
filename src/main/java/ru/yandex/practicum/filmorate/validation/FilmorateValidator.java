package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class FilmorateValidator {

    // TODO Комментарий для код-ревью (удалить после спринта 8) — валидация реализована исходя из следующмх соображений.
    //  Хотелось получать в лог предупреждения обо все ошибках в переданном наборе данных, поэтому исключение
    //  не бросается сразу после выявления первой ошибки. Хотелось, помимо предупреждений в лог о каждой ошибке
    //  валидации в наборе данных, получить итоговое сообщение в лог об ошибке операции с данными от класса контроллера.
    //  Исключение должно выбрасываться в мэппингах контроллера, чтобы @RestController сам сформировал ответ с кодами
    //  ошибок.

    public static boolean validateUser(User user) {

        boolean isValidLogin = null != user.getLogin() && !user.getLogin().isEmpty() && !user.getLogin().contains(" ");
        logWarning("Логин '{}' не должен быть пустым или содержать пробелы", user.getLogin(), isValidLogin);

        boolean isValidEmail = null != user.getEmail() && user.getEmail().contains("@");
        logWarning("Email {} должен содержать символ @", user.getEmail(), isValidEmail);

        boolean isValidBirthday = null == user.getBirthday() || user.getBirthday().isBefore(LocalDate.now());
        logWarning("День рождения пользователя {} не должен быть в будущем", user.getBirthday(),
                isValidBirthday);

        boolean isValidUser = isValidLogin && isValidEmail && isValidBirthday;

        if (isValidUser) {
            validateUserName(user);
        }

        return isValidUser;
    }

    public static boolean validateFilm(Film film) {

        boolean isValidName = null != film.getName() && !film.getName().isEmpty();
        logWarning("Название фильма '{}' не должно быть пустым", film.getName(), isValidName);

        boolean isValidDescription = null == film.getDescription() || film.getDescription().length() <= 200;
        logWarning("Длина описания фильма — {}, максимальная длина — 200 символов",
                (null != film.getDescription()) ? film.getDescription().length() : null, isValidDescription);

        boolean isValidRelease = null == film.getReleaseDate()
                || film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 27));
        logWarning("Дата релиза фильиа {} должна быть не раньше 28.12.1895", film.getReleaseDate(),
                isValidRelease);

        boolean isValidDuration = film.getDuration() > 0;
        logWarning("Продолжительность фильма '{}' должна быть больше 0", film.getDuration(), isValidDuration);

        return isValidName && isValidDescription && isValidRelease && isValidDuration;
    }

    private static void logWarning(String message, Object parameter, boolean condition) {
        if (!condition) {
            log.warn("Ошибка валидации данных: " + message, parameter);
        }
    }

    private static void validateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя id={} не задано, в качестве имени будет использоваться логин '{}'",
                    user.getId(), user.getLogin());
        }
    }
}