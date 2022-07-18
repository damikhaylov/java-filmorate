package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@ToString
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
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private final Set<Long> friends = new HashSet<>();
}