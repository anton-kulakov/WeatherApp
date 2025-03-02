package dev.anton_kulakov.controller;

import dev.anton_kulakov.dto.UserAuthorizationDto;
import dev.anton_kulakov.service.CookieService;
import dev.anton_kulakov.service.SessionService;
import dev.anton_kulakov.validator.RedirectUrlValidator;
import dev.anton_kulakov.validator.UserAuthorizationDtoValidator;
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
    private final UserAuthorizationDtoValidator userAuthorizationDtoValidator;
    private final RedirectUrlValidator redirectUrlValidator;

    @Autowired
    public SignInController(SessionService sessionService, CookieService cookieService, UserAuthorizationDtoValidator userAuthorizationDtoValidator, RedirectUrlValidator redirectUrlValidator) {
        this.sessionService = sessionService;
        this.cookieService = cookieService;
        this.userAuthorizationDtoValidator = userAuthorizationDtoValidator;
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

        userAuthorizationDtoValidator.validate(userAuthorizationDto, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("userAuthorizationDto", userAuthorizationDto);
            return "sign-in";
        }

        UUID uuid = sessionService.create(userAuthorizationDto.getLogin());
        Cookie cookie = cookieService.create(uuid);
        response.addCookie(cookie);
        String redirectToUrl = redirectUrlValidator.getValidRedirectToUrl(redirectTo);

        return "redirect:" + redirectToUrl;
    }
}
