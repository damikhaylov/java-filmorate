package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.Errors;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class FilmValidator extends ObjectValidator<Film> {

    public FilmValidator(Film film, Errors springValidationErrors) {
        super(film, springValidationErrors);
        validateReleaseDate();
    }

    private void validateReleaseDate() {
        if (null != validatedObject.getReleaseDate()
                && validatedObject.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            hasErrors = true;
            errorMessages.add(String.format("В поле releaseDate дата '%s' должна быть не раньше 28.12.1895",
                    validatedObject.getReleaseDate()));
        }
    }
}