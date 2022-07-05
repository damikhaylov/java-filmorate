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
import ru.yandex.practicum.filmorate.controller.FilmController;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = FilmController.class)
public class FilmControllerTest {
    @Autowired
    MockMvc mockMvc;

    private final static LocalDate REGULAR_DATE = LocalDate.of(1985, 5, 20);

    @DisplayName("Тест добавления фильма с валидными данными")
    @ParameterizedTest(name = "{index} При валидных данных фильма запрос на добавление завершается успешно")
    @MethodSource("filmValidParameters")
    void addValidFilmTest(long id, String name, String description, LocalDate release, int duration) throws Exception {

        String filmJson = getFilmJson(id, name, description, release, duration);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @DisplayName("Тест на добавление фильма с неверным в целом запросом")
    @ParameterizedTest(name = "{index} При неверном запросе сервер возвращает ошибку ")
    @MethodSource("filmBadRequest")
    void addBadRequest(String request) throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DisplayName("Тест валидации неверных данных при добавлении фильма")
    @ParameterizedTest(name = "{index} Неверные данные фильма должны вызывать исключение")
    @MethodSource("filmInvalidParameters")
    void addInvalidFilmTest(long id, String name, String description, LocalDate release, int duration) {

        String filmJson = getFilmJson(id, name, description, release, duration);

        NestedServletException exception = assertThrows(NestedServletException.class, ()
                -> mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(filmJson)));

        assertTrue(exception.getMessage() != null
                && exception.getMessage().contains("Данные фильма содержат ошибки и не были добавлены."));
    }

    @DisplayName("Тест обновления фильма с валидными данными")
    @ParameterizedTest(name = "{index} При валидных данных фильма запрос на обновление завершается успешно")
    @MethodSource("filmValidParameters")
    void updateValidFilmTest(long id, String name, String description, LocalDate release, int duration)
            throws Exception {

        addValidFilmTest(1, "OldTesfilm", "Old Test description", REGULAR_DATE, 120);

        String filmJson = getFilmJson(id, name, description, release, duration);

        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @DisplayName("Тест на обновление фильма с неверным в целом запросом")
    @ParameterizedTest(name = "{index} При неверном запросе '{0}' сервер возвращает ошибку ")
    @MethodSource("filmBadRequest")
    void updateBadRequest(String request) throws Exception {

        addValidFilmTest(1, "OldTesfilm", "Old Test description", REGULAR_DATE, 120);

        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DisplayName("Тест валидации неверных данных при обновлении фильма")
    @ParameterizedTest(name = "{index} Неверные данные фильма должны вызывать исключение")
    @MethodSource("filmInvalidParameters")
    void updateInvalidFilmTest(long id, String name, String description, LocalDate release, int duration)
            throws Exception {

        addValidFilmTest(1, "OldTesfilm", "Old test description", REGULAR_DATE, 120);

        String filmJson = getFilmJson(id, name, description, release, duration);

        NestedServletException exception = assertThrows(NestedServletException.class, ()
                -> mockMvc.perform(MockMvcRequestBuilders.put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(filmJson)));

        assertTrue(exception.getMessage() != null
                && exception.getMessage().contains("Данные фильма содержат ошибки и не были обновлены."));
    }

    @DisplayName("Тест обновления фильма с несуществующим id")
    @ParameterizedTest(name = "{index} Обновление с несуществующим id = {0} должно вызывать исключение")
    @MethodSource("filmInvalidIdParameters")
    void updateNonExistentId(long id) throws Exception {

        addValidFilmTest(1, "OldTesfilm", "Old Test description", REGULAR_DATE, 120);

        String filmJson = getFilmJson(id, "Tesfilm", "Test description", REGULAR_DATE, 120);

        NestedServletException exception = assertThrows(NestedServletException.class, ()
                -> mockMvc.perform(MockMvcRequestBuilders.put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(filmJson)));

        assertTrue(exception.getMessage() != null
                && exception.getMessage().contains(String.format("Фильм с id=%d не существует.", id)));
    }

    @DisplayName("Тест запроса на получение списка фильмов")
    @Test
    void getUsersTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.
                        get("/films")).
                andExpect(MockMvcResultMatchers.status().isOk());
    }

    private static Stream<Arguments> filmBadRequest() {
        return Stream.of(
                // Пустое тело запроса
                Arguments.of(""),
                // Некорректный JSON
                Arguments.of("{fruit = apple}")
        );
    }

    private static Stream<Arguments> filmValidParameters() {
        return Stream.of(
                // Нормальный вариант данных
                Arguments.of(1, "Tesfilm", "Test description", REGULAR_DATE, 120),
                // Максимальная длина описания — 200 символов
                Arguments.of(1, "Tesfilm", null, REGULAR_DATE, 120),
                Arguments.of(1, "Tesfilm", "", REGULAR_DATE, 120),
                Arguments.of(1, "Tesfilm", "a".repeat(200), REGULAR_DATE, 120),
                // Дата релиза — не раньше 28 декабря 1895 года
                Arguments.of(1, "Tesfilm", "Test description",
                        LocalDate.of(1895, 12, 28), 120),
                Arguments.of(1, "Tesfilm", "Test description", null, 120),
                // Продолжительность фильма должна быть положительной
                Arguments.of(1, "Tesfilm", "Test description", REGULAR_DATE, 1)
        );
    }

    private static Stream<Arguments> filmInvalidParameters() {
        return Stream.of(
                // Название не может быть пустым
                Arguments.of(1, null, "Test description", REGULAR_DATE, 120),
                Arguments.of(1, "", "Test description", REGULAR_DATE, 120),
                // Максимальная длина описания — 200 символов
                Arguments.of(1, "Tesfilm", "a".repeat(201), REGULAR_DATE, 120),
                // Дата релиза — не раньше 28 декабря 1895 года
                Arguments.of(1, "Tesfilm", "Test description",
                        LocalDate.of(1895, 12, 27), 120),
                // Продолжительность фильма должна быть положительной
                Arguments.of(1, "Tesfilm", "Test description", REGULAR_DATE, 0),
                Arguments.of(1, "Tesfilm", "Test description", REGULAR_DATE, -1)
        );
    }

    private static Stream<Arguments> filmInvalidIdParameters() {
        return Stream.of(
                Arguments.of(-1),
                Arguments.of(Long.MAX_VALUE)
        );
    }

    private static String getFilmJson(long id, String name, String description, LocalDate releaseDate, int duration) {
        StringBuilder jsonStringBuilder = new StringBuilder("{");
        jsonStringBuilder.append("\"id\": ").append(id);
        if (null != name) {
            jsonStringBuilder.append(", \"name\": \"").append(name).append("\"");
        }
        if (null != description) {
            jsonStringBuilder.append(", \"description\": \"").append(description).append("\"");
        }
        if (null != releaseDate) {
            jsonStringBuilder.append(", \"releaseDate\": \"").append(releaseDate).append("\"");
        }
        jsonStringBuilder.append(", \"duration\": ").append(duration);
        jsonStringBuilder.append("}");
        return jsonStringBuilder.toString();
    }
}