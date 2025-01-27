package dev.anton_kulakov.controller;

import dev.anton_kulakov.dao.UserDao;
import dev.anton_kulakov.dto.UserAuthorizationDto;
import dev.anton_kulakov.model.User;
import dev.anton_kulakov.model.UserSession;
import dev.anton_kulakov.service.CookieService;
import dev.anton_kulakov.service.PasswordHashingService;
import dev.anton_kulakov.service.SessionService;
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
@RequestMapping("sign-in")
public class SignInController {
    private final UserDao userDao;
    private final PasswordHashingService passwordHashingService;
    private final SessionService sessionService;
    private final CookieService cookieService;

    @Autowired
    public SignInController(UserDao userDao, PasswordHashingService passwordHashingService, SessionService sessionService, CookieService cookieService) {
        this.userDao = userDao;
        this.passwordHashingService = passwordHashingService;
        this.sessionService = sessionService;
        this.cookieService = cookieService;
    }

    @GetMapping
    public String doGet(Model model) {
        model.addAttribute("userAuthorizationDto", new UserAuthorizationDto());
        return "sign-in";
    }

    @PostMapping
    public String doPost(Model model,
                         @ModelAttribute("userAuthorizationDto") @Valid UserAuthorizationDto userAuthorizationDto,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         BindingResult bindingResult,
                         @RequestParam("redirect_to") String redirectTo) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("userAuthorizationDto", new UserAuthorizationDto());
            return "sign-in";
        }

        Optional<User> optionalUser = userDao.getByLogin(userAuthorizationDto.getLogin());

        if (optionalUser.isEmpty()) {
            bindingResult.rejectValue("login", "error.loginNonExistence", "The user with this username does not exist. Please create an account first");
            model.addAttribute("userAuthorizationDto", new UserAuthorizationDto());
            return "sign-in";
        }

        User user = optionalUser.get();

        if (!passwordHashingService.isPasswordValid(userAuthorizationDto.getPassword(), user.getPassword())) {
            bindingResult.rejectValue("password", "error.incorrectPassword", "The password you entered is incorrect. Please try again.");
            model.addAttribute("userAuthorizationDto", new UserAuthorizationDto());
            return "sign-in";
        }

        sessionService.deleteExpiredSessions();
        Optional<Cookie> foundCookieOptional = cookieService.findCookieByUuidName(request.getCookies());
        Optional<UserSession> userSessionOptional = Optional.empty();

        if (foundCookieOptional.isPresent()) {
            userSessionOptional = sessionService.get(foundCookieOptional.get());
        }

        if (userSessionOptional.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            sessionService.persist(user, uuid);
            Cookie cookie = cookieService.createCookie(uuid);
            response.addCookie(cookie);
        }

        return "redirect:" + redirectTo;
    }
}
