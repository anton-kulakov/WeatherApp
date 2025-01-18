package dev.anton_kulakov.service;

import dev.anton_kulakov.config.TestConfig;
import dev.anton_kulakov.dao.UserDao;
import dev.anton_kulakov.dto.UserRegistrationDto;
import dev.anton_kulakov.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringJUnitConfig(classes = TestConfig.class)
@WebAppConfiguration
@Transactional
public class UserServiceIT {
    UserService userService;
    UserDao userDao;

    @Autowired
    public UserServiceIT(UserService userService, UserDao userDao) {
        this.userService = userService;
        this.userDao = userDao;
    }

    @Test
    @DisplayName("After registration, the new user should be saved in the database")
    void shouldSaveNewUserInDatabaseAfterRegistration() {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setLogin("test-login");
        userRegistrationDto.setPassword("test-password");

        userService.persist(userRegistrationDto);

        Assertions.assertTrue(userDao.getByLogin("test-login").isPresent());
    }

    @Test
    @DisplayName("When trying to save a user that already exists, an error should be thrown")
    void shouldThrowExceptionWhenTryingToSaveExistingUser() {
        User user = new User("test-login", "test-password");
        userDao.persist(user);

        IllegalArgumentException thrownException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> userDao.persist(user));

        Assertions.assertEquals("User with this login already exists", thrownException.getMessage());
    }
}


