package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.Errors;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
public class UserValidator extends ObjectValidator<User> {

    public UserValidator(User user, Errors springValidationErrors) {
        super(user, springValidationErrors);
        validateLogin();
        if (!hasErrors) {
            validateUserName();
        }
    }

    private void validateLogin() {
        if (null == validatedObject.getLogin() || validatedObject.getLogin().contains(" ")) {
            hasErrors = true;
            errorMessages.add(String.format("Поле login '%s' не должно содержать пробелы", validatedObject.getLogin()));
        }
    }

    private void validateUserName() {
        if (validatedObject.getName() == null || validatedObject.getName().isBlank()) {
            validatedObject.setName(validatedObject.getLogin());
            log.info("Имя пользователя id={} не задано, в качестве имени будет использоваться логин '{}'",
                    validatedObject.getId(), validatedObject.getLogin());
        }
    }
}
