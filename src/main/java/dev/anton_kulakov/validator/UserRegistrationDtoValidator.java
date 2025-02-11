package dev.anton_kulakov.validator;

import dev.anton_kulakov.dto.UserRegistrationDto;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserRegistrationDtoValidator implements Validator {
    @Override
    public boolean supports(@NonNull Class<?> aClass) {
        return UserRegistrationDto.class.equals(aClass);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        UserRegistrationDto userRegistrationDto = (UserRegistrationDto) target;

        String password = userRegistrationDto.getPassword();
        String confirmPassword = userRegistrationDto.getConfirmPassword();
        String errorMessage = "Password and confirmation do not match";

        if (!password.equals(confirmPassword)) {
            errors.rejectValue("password", "", errorMessage);
            errors.rejectValue("confirmPassword", "", errorMessage);
        }
    }
}
