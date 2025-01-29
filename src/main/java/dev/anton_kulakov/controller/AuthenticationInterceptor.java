package dev.anton_kulakov.controller;

import dev.anton_kulakov.dao.UserDao;
import dev.anton_kulakov.dto.UserRequestDto;
import dev.anton_kulakov.model.User;
import dev.anton_kulakov.model.UserSession;
import dev.anton_kulakov.service.CookieService;
import dev.anton_kulakov.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

public class AuthenticationInterceptor implements HandlerInterceptor {
    private final CookieService cookieService;
    private final SessionService sessionService;
    private final UserDao userDao;

    @Autowired
    public AuthenticationInterceptor(CookieService cookieService, SessionService sessionService, UserDao userDao) {
        this.cookieService = cookieService;
        this.sessionService = sessionService;
        this.userDao = userDao;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        Optional<Cookie> cookieOptional = cookieService.getCookieByName(request.getCookies(), "uuid");

        if (cookieOptional.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/sign-in");
            return false;
        }

        Optional<UserSession> userSessionOptional = sessionService.get(cookieOptional.get());

        if (userSessionOptional.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/sign-in");
            return false;
        }

        int userId = userSessionOptional.get().getUserID();
        Optional<User> userOptional = userDao.getById(userId);

        if (userOptional.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/sign-up");
            return false;
        }

        User user = userOptional.get();
        request.setAttribute("userRequestDto", new UserRequestDto(user.getId(), user.getLogin()));

        return true;
    }
}
