package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

// TODO Комментарий для код-ревью (удалить после спринта 8) — аннотации Bean Validation API проставлены по
//  дополнительному заданию из ТЗ, работа приложения с ними была локально протестирована через Postman.
//  Сейчас аннотации закомментированы, чтобы при тестировании GitHub Actions все проверки отрабатывались методами
//  из класса FilmorateValidator, которые были написанны по основной части техзадания.
//  Как будет реализована валидация в итоге, хотелось бы решить на основе тз к дальнейшим частям проекта.
//  Стандартные constraint'ы  Bean Validation API не покрывают всей необходимой валидации (отсутствие пробелов в логине,
//  дата релиза). При этом не хочется сочетать встроенные проверки  Bean Validation API с собственными простыми
//  проверками которые работают сейчас, чтобы не было разнобоя в точках выполнения проверок, в логах и т.п.
//  Если в будущих спринтах будет дан материал по созданию собственных constraint'ов, то предполагаю реализовать
//  все проверки с их помощью.
@Data
@AllArgsConstructor
public class Film {
    private int id;
    //@NotNull
    //@NotEmpty
    private String name;
    //@Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    //@Positive
    private int duration;
}