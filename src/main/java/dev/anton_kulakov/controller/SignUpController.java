package dev.anton_kulakov.controller;

import dev.anton_kulakov.dto.UserAuthorizationDto;
import dev.anton_kulakov.dto.UserRegistrationDto;
import dev.anton_kulakov.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sign-up")
public class SignUpController {
    private final UserService userService;

    @Autowired
    public SignUpController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String doGet(Model model) {
        model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        return "sign-up";
    }

    @PostMapping
    public String doPost(Model model,
                         @ModelAttribute("userRegistrationDto") @Valid UserRegistrationDto userRegistrationDto,
                         BindingResult bindingResult,
                         @RequestParam("redirect_to") String redirectTo) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("userAuthorizationDto", new UserAuthorizationDto());
            return "sign-up";
        }

        if (!userRegistrationDto.getPassword().equals(userRegistrationDto.getConfirmPassword())) {
            String errorMessage = "Password and confirmation do not match";
            bindingResult.rejectValue("password", "error.passwordMismatch", errorMessage);
            bindingResult.rejectValue("confirmPassword", "error.passwordConfirmationMismatch", errorMessage);
            model.addAttribute("userAuthorizationDto", new UserAuthorizationDto());
            return "sign-up";
        }

        try {
            userService.persist(userRegistrationDto);
            return "redirect:" + redirectTo;
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("login", "error.login", e.getMessage());
            model.addAttribute("userAuthorizationDto", new UserAuthorizationDto());
            return "sign-up";
        } catch (Exception e) {
            bindingResult.rejectValue("globalError", "An unexpected error occurred");
            model.addAttribute("userAuthorizationDto", new UserAuthorizationDto());
            return "sign-up";
        }
    }
}
