package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class FilmorateValidator {

    // TODO Комментарий для код-ревью (удалить после спринта 8) — валидация реализована исходя из следующих соображений.
    //  Хотелось получать в лог предупреждения обо все ошибках в переданном наборе данных, поэтому исключение
    //  не бросается сразу после выявления первой ошибки. Хотелось, помимо предупреждений в лог о каждой ошибке
    //  валидации в наборе данных, получить итоговое сообщение в лог об ошибке операции с данными от класса контроллера.
    //  Исключение должно выбрасываться в мэппингах контроллера, чтобы @RestController сам сформировал ответ с кодами
    //  ошибок.
    //  ++ Заменил собственные проверки валидацией Spring, где это было возможно, но сделал для них и для оставшихся
    //  собственных проверок общее логирование и дальнейшую обработку.

    public static boolean validateUser(User user, Errors springValidationErrors) {
        springValidationErrors.getGlobalErrors().forEach(FilmorateValidator::logSpringValidationGlobalError);
        springValidationErrors.getFieldErrors().forEach(FilmorateValidator::logSpringValidationFieldError);

        boolean isValidLogin = null != user.getLogin() && !user.getLogin().contains(" ");
        logValidationWarning("Поле login '{}' не должно содержать пробелы", user.getLogin(), isValidLogin);

        boolean isValidUser = !springValidationErrors.hasErrors() && isValidLogin;

        if (isValidUser) {
            validateUserName(user);
        }

        return isValidUser;
    }

    public static boolean validateFilm(Film film, Errors springValidationErrors) {
        springValidationErrors.getGlobalErrors().forEach(FilmorateValidator::logSpringValidationGlobalError);
        springValidationErrors.getFieldErrors().forEach(FilmorateValidator::logSpringValidationFieldError);

        boolean isValidRelease = null == film.getReleaseDate()
                || film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 27));
        logValidationWarning("Дата релиза фильма {} должна быть не раньше 28.12.1895", film.getReleaseDate(),
                isValidRelease);

        return !springValidationErrors.hasErrors() && isValidRelease;
    }

    private static void logValidationWarning(String message, Object parameter, boolean condition) {
        if (!condition) {
            log.warn("Ошибка валидации данных: " + message, parameter);
        }
    }

    private static void logSpringValidationGlobalError(ObjectError error) {
        log.warn("Ошибка валидации данных (Spring): {}", error.getDefaultMessage());
    }

    private static void logSpringValidationFieldError(FieldError error) {
        log.warn("Ошибка валидации данных (Spring): Поле {} '{}' {}",
                error.getField(), error.getRejectedValue(), error.getDefaultMessage());
    }

    private static void validateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя id={} не задано, в качестве имени будет использоваться логин '{}'",
                    user.getId(), user.getLogin());
        }
    }
}