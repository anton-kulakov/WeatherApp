package dev.anton_kulakov.controller;

import dev.anton_kulakov.dao.UserDao;
import dev.anton_kulakov.dto.UserRegistrationDto;
import dev.anton_kulakov.model.User;
import dev.anton_kulakov.service.PasswordHashingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sign-up")
public class RegistrationController {
    private final UserDao userDao;
    private final PasswordHashingService passwordHashingService;

    @Autowired
    public RegistrationController(UserDao userDao, PasswordHashingService passwordHashingService) {
        this.userDao = userDao;
        this.passwordHashingService = passwordHashingService;
    }

    @GetMapping
    public String doGet(Model model) {
        model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        return "sign-up";
    }

    @PostMapping
    public String doPost(@ModelAttribute("userRegistrationDto") @Valid UserRegistrationDto userRegistrationDto,
                         BindingResult bindingResult,
                         @RequestParam("redirect_to") String redirectTo) {

        if (bindingResult.hasErrors()) {
            return "sign-up";
        }

        if (!userRegistrationDto.getPassword().equals(userRegistrationDto.getConfirmPassword())) {
            String errorMessage = "Password and confirmation do not match";
            bindingResult.rejectValue("password", "error.passwordMismatch", errorMessage);
            bindingResult.rejectValue("confirmPassword", "error.passwordConfirmationMismatch", errorMessage);
            return "sign-up";
        }

        try {
            String login = userRegistrationDto.getLogin();
            String hashedPassword = passwordHashingService.hashPassword(userRegistrationDto.getPassword());
            userDao.persist(new User(login, hashedPassword));

            return "redirect:" + redirectTo;
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("login", "error.login", e.getMessage());
            return "sign-up";
        } catch (Exception e) {
            bindingResult.rejectValue("globalError", "An unexpected error occurred");
            return "sign-up";
        }
    }
}
