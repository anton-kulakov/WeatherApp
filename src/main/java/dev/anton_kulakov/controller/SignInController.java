package dev.anton_kulakov.controller;

import dev.anton_kulakov.dao.UserDao;
import dev.anton_kulakov.dto.UserAuthorizationDto;
import dev.anton_kulakov.model.User;
import dev.anton_kulakov.model.UserSession;
import dev.anton_kulakov.service.CookieService;
import dev.anton_kulakov.service.SessionService;
import dev.anton_kulakov.validator.UserAuthDtoValidator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/sign-in")
public class SignInController {
    private final UserDao userDao;
    private final SessionService sessionService;
    private final CookieService cookieService;
    private final UserAuthDtoValidator userAuthDtoValidator;

    @Autowired
    public SignInController(UserDao userDao, SessionService sessionService, CookieService cookieService, UserAuthDtoValidator userAuthDtoValidator) {
        this.userDao = userDao;
        this.sessionService = sessionService;
        this.cookieService = cookieService;
        this.userAuthDtoValidator = userAuthDtoValidator;
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
                         HttpServletRequest request,
                         HttpServletResponse response,
                         @RequestParam("redirect_to") String redirectTo) {

        userAuthDtoValidator.validate(userAuthorizationDto, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("userAuthorizationDto", userAuthorizationDto);
            return "sign-in";
        }

        sessionService.deleteExpiredSessions();
        Optional<Cookie> foundCookieOptional = cookieService.getCookieByName(request.getCookies(), "uuid");
        Optional<UserSession> userSessionOptional = Optional.empty();

        if (foundCookieOptional.isPresent()) {
            userSessionOptional = sessionService.get(foundCookieOptional.get());
        }

        Optional<User> optionalUser = userDao.getByLogin(userAuthorizationDto.getLogin());
        User user = optionalUser.get();

        if (userSessionOptional.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            sessionService.persist(user, uuid);
            Cookie cookie = cookieService.createCookie(uuid);
            response.addCookie(cookie);
        }

        return "redirect:" + redirectTo;
    }
}
