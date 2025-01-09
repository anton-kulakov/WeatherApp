package dev.anton_kulakov.service;

import dev.anton_kulakov.dao.UserDao;
import dev.anton_kulakov.dto.UserRegistrationDto;
import dev.anton_kulakov.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserDao userDao;
    private final PasswordHashingService passwordHashingService;

    @Autowired
    public UserService(PasswordHashingService passwordHashingService, UserDao userDao) {
        this.userDao = userDao;
        this.passwordHashingService = passwordHashingService;
    }

    public void createUser(UserRegistrationDto userRegistrationDto) {
        String login = userRegistrationDto.getLogin();
        String hashedPassword = passwordHashingService.hashPassword(userRegistrationDto.getPassword());
        userDao.persist(new User(login, hashedPassword));
    }
}
