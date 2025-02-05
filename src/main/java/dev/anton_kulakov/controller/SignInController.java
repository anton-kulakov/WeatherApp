package dev.anton_kulakov.controller;

import dev.anton_kulakov.dto.UserAuthorizationDto;
import dev.anton_kulakov.service.CookieService;
import dev.anton_kulakov.service.SessionService;
import dev.anton_kulakov.validator.RedirectUrlValidator;
import dev.anton_kulakov.validator.UserAuthDtoValidator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/sign-in")
public class SignInController {
    private final SessionService sessionService;
    private final CookieService cookieService;
    private final UserAuthDtoValidator userAuthDtoValidator;
    private final RedirectUrlValidator redirectUrlValidator;

    @Autowired
    public SignInController(SessionService sessionService, CookieService cookieService, UserAuthDtoValidator userAuthDtoValidator, RedirectUrlValidator redirectUrlValidator) {
        this.sessionService = sessionService;
        this.cookieService = cookieService;
        this.userAuthDtoValidator = userAuthDtoValidator;
        this.redirectUrlValidator = redirectUrlValidator;
    }

    @GetMapping
    public String doGet(Model model) {
        model.addAttribute("userAuthorizationDto", new UserAuthorizationDto());
        return "sign-in";
    }

    @PostMapping
    public String doPost(Model model,
                         @ModelAttribute("userAuthorizationDto") @Valid UserAuthorizationDto userAuthorizationDto,
                         BindingResult bindingResult,
                         HttpServletResponse response,
                         @RequestParam("redirect_to") String redirectTo) {

        userAuthDtoValidator.validate(userAuthorizationDto, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("userAuthorizationDto", userAuthorizationDto);
            return "sign-in";
        }

        if (!redirectUrlValidator.isRedirectUrlValid(redirectTo)) {
            redirectTo = "/index";
        }

        UUID uuid = sessionService.create(userAuthorizationDto.getLogin());
        Cookie cookie = cookieService.create(uuid);
        response.addCookie(cookie);

        return "redirect:" + redirectTo;
    }
}
