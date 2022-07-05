package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    private long id;
    @NotNull
    @NotBlank
    private String login;
    @NotNull
    @NotBlank
    @Email
    private String email;
    private String name;
    @Past
    private LocalDate birthday;
}