package dev.anton_kulakov.controller;

import dev.anton_kulakov.dao.SessionDao;
import dev.anton_kulakov.dao.UserDao;
import dev.anton_kulakov.dto.UserAuthorizationDto;
import dev.anton_kulakov.model.User;
import dev.anton_kulakov.model.UserSession;
import dev.anton_kulakov.service.PasswordHashingService;
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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("sign-in")
public class SignInController {
    private final UserDao userDao;
    private final SessionDao sessionDao;
    private final PasswordHashingService passwordHashingService;

    @Autowired
    public SignInController(UserDao userDao, SessionDao sessionDao, PasswordHashingService passwordHashingService) {
        this.userDao = userDao;
        this.sessionDao = sessionDao;
        this.passwordHashingService = passwordHashingService;
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

        sessionDao.deleteExpiredSessions();

        if (!isUserSessionExist(request.getCookies())) {
            UUID uuid = UUID.randomUUID();
            UserSession userSession = new UserSession(uuid.toString(), user.getId(), LocalDateTime.now().plusHours(24));

            sessionDao.persist(userSession);

            Cookie cookie = new Cookie("uuid", uuid.toString());
            int SECONDS_IN_A_DAY = 24 * 60 * 60;
            cookie.setMaxAge(SECONDS_IN_A_DAY);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }

        return "redirect:" + request.getParameter("redirect-to");
    }

    private boolean isUserSessionExist(Cookie[] cookies) {
        if (cookies == null) {
            return false;
        }

        for (Cookie cookie : cookies) {
            if (!("uuid".equals(cookie.getName()))) {
                return false;
            }
        }

        for (Cookie cookie : cookies) {
            UUID uuid = UUID.fromString(cookie.getName());

            if (sessionDao.countById(uuid) == 0) {
                return false;
            }
        }

        return true;
    }
}
