package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.NestedServletException;
import ru.yandex.practicum.filmorate.controller.UserController;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    private final static LocalDate REGULAR_DATE = LocalDate.of(1985, 5, 20);

    @DisplayName("Тест добавления пользователя с валидными данными")
    @ParameterizedTest(name = "{index} При валидных данных пользователя запрос на добавление завершается успешно")
    @MethodSource("userValidParameters")
    void addValidUserTest(long id, String login, String email, String name, LocalDate birthday) throws Exception {

        String userJson = getUserJson(id, login, email, name, birthday);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @DisplayName("Тест на добавление пользователя с неверным в целом запросом")
    @ParameterizedTest(name = "{index} При неверном запросе сервер возвращает ошибку ")
    @MethodSource("userBadRequest")
    void addBadRequest(String request) throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DisplayName("Тест валидации неверных данных при добавлении пользователя")
    @ParameterizedTest(name = "{index} Неверные данные пользователя должны вызывать исключение")
    @MethodSource("userInvalidParameters")
    void addInvalidUserTest(long id, String login, String email, String name, LocalDate birthday) {

        String userJson = getUserJson(id, login, email, name, birthday);

        NestedServletException exception = assertThrows(NestedServletException.class, ()
                -> mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)));

        assertTrue(exception.getMessage() != null
                && exception.getMessage().contains("Данные пользователя содержат ошибки и не были добавлены."));
    }

    @DisplayName("Тест обновления пользователя с валидными данными")
    @ParameterizedTest(name = "{index} При валидных данных пользователя запрос на обновление завершается успешно")
    @MethodSource("userValidParameters")
    void updateValidUserTest(long id, String login, String email, String name, LocalDate birthday) throws Exception {

        addValidUserTest(1, "testuser_old", "emailold@test.com", "Test User Old", REGULAR_DATE);

        String userJson = getUserJson(id, login, email, name, birthday);

        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @DisplayName("Тест на обновление пользователя с неверным в целом запросом")
    @ParameterizedTest(name = "{index} При неверном запросе '{0}' сервер возвращает ошибку ")
    @MethodSource("userBadRequest")
    void updateBadRequest(String request) throws Exception {

        addValidUserTest(1, "testuser_old", "emailold@test.com", "Test User Old", REGULAR_DATE);

        mockMvc.perform(MockMvcRequestBuilders.put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DisplayName("Тест валидации неверных данных при обновлении пользователя")
    @ParameterizedTest(name = "{index} Неверные данные пользователя должны вызывать исключение")
    @MethodSource("userInvalidParameters")
    void updateInvalidUserTest(long id, String login, String email, String name, LocalDate birthday) throws Exception {

        addValidUserTest(1, "testuser_old", "emailold@test.com", "Test User Old", REGULAR_DATE);

        String userJson = getUserJson(id, login, email, name, birthday);

        NestedServletException exception = assertThrows(NestedServletException.class, ()
                -> mockMvc.perform(MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)));

        assertTrue(exception.getMessage() != null
                && exception.getMessage().contains("Данные пользователя содержат ошибки и не были обновлены."));
    }

    @DisplayName("Тест обновления пользователя с несуществующим id")
    @ParameterizedTest(name = "{index} Обновление с несуществующим id = {0} должно вызывать исключение")
    @MethodSource("userInvalidIdParameters")
    void updateNonExistentId(long id) throws Exception {

        addValidUserTest(1, "testuser_old", "emailold@test.com", "Test User Old", REGULAR_DATE);

        String userJson = getUserJson(id, "testuser", "email@test.com", "Test User", REGULAR_DATE);

        NestedServletException exception = assertThrows(NestedServletException.class, ()
                -> mockMvc.perform(MockMvcRequestBuilders.put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)));

        assertTrue(exception.getMessage() != null
                && exception.getMessage().contains(String.format("Пользователь с id=%d не существует.", id)));
    }

    @DisplayName("Тест запроса на получение списка пользователей")
    @Test
    void getUsersTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.
                        get("/users")).
                andExpect(MockMvcResultMatchers.status().isOk());
    }

    private static Stream<Arguments> userBadRequest() {
        return Stream.of(
                // Пустое тело запроса
                Arguments.of(""),
                // Некорректный JSON
                Arguments.of("{fruit = apple}")
        );
    }

    private static Stream<Arguments> userValidParameters() {
        return Stream.of(
                // Нормальный вариант данных
                Arguments.of(1, "testuser", "email@test.com", "Test User", REGULAR_DATE),
                // Граничные тесты по дате рождения
                Arguments.of(1, "testuser", "email@test.com", "Test User",
                        LocalDate.now().minusDays(1)),
                Arguments.of(1, "testuser", "email@test.com", "Test User", null),
                // Тесты с пустым именем
                Arguments.of(1, "testuser", "email@test.com", null, REGULAR_DATE),
                Arguments.of(1, "testuser", "email@test.com", "", REGULAR_DATE)
        );
    }

    private static Stream<Arguments> userInvalidParameters() {
        return Stream.of(
                // Логин не может быть пустым и содержать пробелы
                Arguments.of(1, null, "email@test.com", "Test User", REGULAR_DATE),
                Arguments.of(1, "", "email@test.com", "Test User", REGULAR_DATE),
                Arguments.of(1, "test user", "email@test.com", "Test User", REGULAR_DATE),
                // Электронная почта не может быть пустой и должна содержать символ @
                Arguments.of(1, "testuser", null, "Test User", REGULAR_DATE),
                Arguments.of(1, "testuser", "", "Test User", REGULAR_DATE),
                Arguments.of(1, "testuser", "email.test.com", "Test User", REGULAR_DATE),
                // Дата рождения не может быть в будущем
                Arguments.of(1, "testuser", "email@test.com", "Test User",
                        LocalDate.now().plusDays(1)),
                Arguments.of(1, "testuser", "email@test.com", "Test User",
                        LocalDate.of(2050, 2, 21))
        );
    }

    private static Stream<Arguments> userInvalidIdParameters() {
        return Stream.of(
                Arguments.of(-1),
                Arguments.of(Long.MAX_VALUE)
        );
    }

    private static String getUserJson(long id, String login, String email, String name, LocalDate birthday) {
        StringBuilder jsonStringBuilder = new StringBuilder("{");
        jsonStringBuilder.append("\"id\": ").append(id);
        if (null != login) {
            jsonStringBuilder.append(", \"login\": \"").append(login).append("\"");
        }
        if (null != email) {
            jsonStringBuilder.append(", \"email\": \"").append(email).append("\"");
        }
        if (null != name) {
            jsonStringBuilder.append(", \"name\": \"").append(name).append("\"");
        }
        if (null != birthday) {
            jsonStringBuilder.append(", \"birthday\": \"").append(birthday).append("\"");
        }
        jsonStringBuilder.append("}");
        return jsonStringBuilder.toString();
    }
}