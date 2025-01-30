package dev.anton_kulakov.service;

import dev.anton_kulakov.dao.UserDao;
import dev.anton_kulakov.dto.UserRegistrationDto;
import dev.anton_kulakov.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserDao userDao;
    private final PasswordService passwordService;

    @Autowired
    public UserService(PasswordService passwordService, UserDao userDao) {
        this.userDao = userDao;
        this.passwordService = passwordService;
    }

    public void persist(UserRegistrationDto userRegistrationDto) {
        String login = userRegistrationDto.getLogin();
        String hashedPassword = passwordService.hashPassword(userRegistrationDto.getPassword());
        userDao.persist(new User(login, hashedPassword));
    }
}
