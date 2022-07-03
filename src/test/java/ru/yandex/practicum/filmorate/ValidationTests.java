package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.validation.FilmorateValidator.validateFilm;
import static ru.yandex.practicum.filmorate.validation.FilmorateValidator.validateUser;

public class ValidationTests {
    private final static LocalDate REGULAR_DATE = LocalDate.of(1985, 5, 20);


    @DisplayName("Тест валидации данных пользователя")
    @ParameterizedTest(name = "{index} Валидация пользователя: {4}")
    @MethodSource("userParameters")
    void userValidationTest(String login, String email, String name, LocalDate birthday, boolean isValid) {

        User user = new User(1, login, email, name, birthday);

        assertEquals(isValid, validateUser(user),
                "Результат вадидации данных пользователя не соответствует вводным данным.");
    }

    @DisplayName("Тест подстановки логина в качестве имени пользователя, если имя пустое")
    @Test
    void emptyUserNameValidationTest() {

        User user = new User(1, "testuser", "email@test.com", "", REGULAR_DATE);

        boolean isValid = (validateUser(user));

        assertTrue(isValid, "Валидация пользователя возвращает false при пустом имени пользователя.");
        assertEquals(user.getLogin(), user.getName(),
                "После валидации пустое имя пользователя не заполнено значением логина.");
    }

    @DisplayName("Тест подстановки логина в качестве имени пользователя, если имя null")
    @Test
    void nullUserNameValidationTest() {

        User user = new User(1, "testuser", "email@test.com", null, REGULAR_DATE);

        boolean isValid = (validateUser(user));

        assertTrue(isValid, "Валидация пользователя возвращает false при пустом имени пользователя.");
        assertEquals(user.getLogin(), user.getName(),
                "После валидации пустое имя пользователя не заполнено значением логина.");
    }

    @DisplayName("Тест валидации данных по фильму")
    @ParameterizedTest(name = "{index} Валидация данных по фильму: {4}")
    @MethodSource("filmParameters")
    void filmValidationTest(String name, String description, LocalDate release, int duration, boolean isValid) {

        Film film = new Film(1, name, description, release, duration);

        assertEquals(isValid, validateFilm(film),
                "Результат вадидации данных фильма не соответствует вводным данным.");
    }

    private static Stream<Arguments> userParameters() {
        return Stream.of(
                // Корректные данные пользователя
                Arguments.of("testuser", "email@test.com", "Test User", REGULAR_DATE, true),
                Arguments.of("testuser", "email@test.com", "Test User",
                        LocalDate.now().minusDays(1), true),
                // Логин не может быть пустым и содержать пробелы
                Arguments.of(null, "email@test.com", "Test User", REGULAR_DATE, false),
                Arguments.of("", "email@test.com", "Test User", REGULAR_DATE, false),
                Arguments.of("test user", "email@test.com", "Test User", REGULAR_DATE, false),
                // Электронная почта не может быть пустой и должна содержать символ @
                Arguments.of("testuser", null, "Test User", REGULAR_DATE, false),
                Arguments.of("testuser", "", "Test User", REGULAR_DATE, false),
                Arguments.of("testuser", "email.test.com", "Test User", REGULAR_DATE, false),
                // Дата рождения не может быть в будущем
                Arguments.of("testuser", "email@test.com", "Test User", null, true),
                Arguments.of("testuser", "email@test.com", "Test User",
                        LocalDate.now().plusDays(1), false),
                Arguments.of("testuser", "email@test.com", "Test User",
                        LocalDate.of(2050, 2, 21), false)
        );
    }

    private static Stream<Arguments> filmParameters() {
        return Stream.of(
                // Корректные данные по фильму
                Arguments.of("Tesfilm", "Test description", REGULAR_DATE, 120, true),
                // Название не может быть пустым
                Arguments.of(null, "Test description", REGULAR_DATE, 120, false),
                Arguments.of("", "Test description", REGULAR_DATE, 120, false),
                // Максимальная длина описания — 200 символов
                Arguments.of("Tesfilm", null, REGULAR_DATE, 120, true),
                Arguments.of("Tesfilm", "", REGULAR_DATE, 120, true),
                Arguments.of("Tesfilm", "a".repeat(200), REGULAR_DATE, 120, true),
                Arguments.of("Tesfilm", "a".repeat(201), REGULAR_DATE, 120, false),
                // Дата релиза — не раньше 28 декабря 1895 года
                Arguments.of("Tesfilm", "Test description", null, 120, true),
                Arguments.of("Tesfilm", "Test description",
                        LocalDate.of(1895, 12, 28), 120, true),
                Arguments.of("Tesfilm", "Test description",
                        LocalDate.of(1895, 12, 27), 120, false),
                // Продолжительность фильма должна быть положительной
                Arguments.of("Tesfilm", "Test description", REGULAR_DATE, 1, true),
                Arguments.of("Tesfilm", "Test description", REGULAR_DATE, 0, false),
                Arguments.of("Tesfilm", "Test description", REGULAR_DATE, -1, false)
        );
    }
}
