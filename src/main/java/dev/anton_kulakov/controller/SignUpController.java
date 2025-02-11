package dev.anton_kulakov.controller;

import dev.anton_kulakov.dto.UserRegistrationDto;
import dev.anton_kulakov.service.UserService;
import dev.anton_kulakov.validator.RedirectUrlValidator;
import dev.anton_kulakov.validator.UserRegistrationDtoValidator;
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
    private final UserRegistrationDtoValidator userRegistrationDtoValidator;
    private final RedirectUrlValidator redirectUrlValidator;

    @Autowired
    public SignUpController(UserService userService, UserRegistrationDtoValidator userRegistrationDtoValidator, RedirectUrlValidator redirectUrlValidator) {
        this.userService = userService;
        this.userRegistrationDtoValidator = userRegistrationDtoValidator;
        this.redirectUrlValidator = redirectUrlValidator;
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

        userRegistrationDtoValidator.validate(userRegistrationDto, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("userRegistrationDto", userRegistrationDto);
            return "sign-up";
        }

        userService.persist(userRegistrationDto);
        String redirectToUrl = redirectUrlValidator.getValidRedirectToUrl(redirectTo);

        return "redirect:" + redirectToUrl;
    }
}
