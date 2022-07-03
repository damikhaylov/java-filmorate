package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

// TODO Комментарий для код-ревью (удалить после спринта 8) — комментарий по поводу закомментированных аннотаций
//  Bean Validation API дан в классе Film.
@Data
@AllArgsConstructor
public class User {
    private int id;
    //@NotNull
    //@NotBlank
    private String login;
    //@NotNull
    //@Email
    private String email;
    private String name;
    //@Past
    private LocalDate birthday;
}