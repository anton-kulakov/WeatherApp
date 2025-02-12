package dev.anton_kulakov.validator;

import dev.anton_kulakov.dao.UserDao;
import dev.anton_kulakov.dto.UserAuthorizationDto;
import dev.anton_kulakov.model.User;
import dev.anton_kulakov.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class UserAuthorizationDtoValidator implements Validator {

    private final UserDao userDao;
    private final PasswordService passwordService;

    @Autowired
    public UserAuthorizationDtoValidator(UserDao userDao, PasswordService passwordService) {
        this.userDao = userDao;
        this.passwordService = passwordService;
    }

    @Override
    public boolean supports(@NonNull Class<?> aClass) {
        return UserAuthorizationDto.class.equals(aClass);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        UserAuthorizationDto userAuthorizationDto = (UserAuthorizationDto) target;
        String login = userAuthorizationDto.getLogin();
        String enteredPassword = userAuthorizationDto.getPassword();

        Optional<User> optionalUser = userDao.getByLogin(login);

        if (optionalUser.isEmpty()) {
            errors.rejectValue("login", "", "The user with this username does not exist. Please create an account first");
            return;
        }

        User user = optionalUser.get();

        if (!passwordService.isPasswordValid(enteredPassword, user.getPassword())) {
            errors.rejectValue("password", "", "The password you entered is incorrect. Please try again");
        }
    }
}
