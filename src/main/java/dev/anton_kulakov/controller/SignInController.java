package dev.anton_kulakov.controller;

import dev.anton_kulakov.dao.UserDao;
import dev.anton_kulakov.dto.UserAuthorizationDto;
import dev.anton_kulakov.model.User;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("sign-in")
public class SignInController {
    private final UserDao userDao;
    private final PasswordHashingService passwordHashingService;
    private final SessionService sessionService;

    @Autowired
    public SignInController(UserDao userDao, PasswordHashingService passwordHashingService, SessionService sessionService) {
        this.userDao = userDao;
        this.passwordHashingService = passwordHashingService;
        this.sessionService = sessionService;
    }

    @GetMapping
    public String doGet(Model model) {
        model.addAttribute("userAuthorizationDto", new UserAuthorizationDto());
        return "sign-in";
    }

    @PostMapping
    public String doPost(@ModelAttribute("userAuthorizationDto") @Valid UserAuthorizationDto userAuthorizationDto,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "sign-in";
        }

        Optional<User> optionalUser = userDao.getByLogin(userAuthorizationDto.getLogin());

        if (optionalUser.isEmpty()) {
            bindingResult.rejectValue("login", "error.loginNonExistence", "The user with this username does not exist. Please create an account first");
            return "sign-in";
        }

        User user = optionalUser.get();

        if (!passwordHashingService.isPasswordValid(userAuthorizationDto.getPassword(), user.getPassword())) {
            bindingResult.rejectValue("password", "error.incorrectPassword", "The password you entered is incorrect. Please try again.");
            return "sign-in";
        }

        sessionService.deleteExpiredSessions();

        if (!sessionService.isUserSessionExist(request.getCookies())) {
            UUID uuid = UUID.randomUUID();
            sessionService.persist(user, uuid);
            Cookie cookie = sessionService.createCookie(uuid);
            response.addCookie(cookie);
        }

        return "redirect:" + request.getParameter("redirect-to");
    }
}
