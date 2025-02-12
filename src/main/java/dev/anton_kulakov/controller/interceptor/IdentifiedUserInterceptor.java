package dev.anton_kulakov.controller.interceptor;

import dev.anton_kulakov.exception.UserNotFoundException;
import dev.anton_kulakov.model.User;
import dev.anton_kulakov.model.UserSession;
import dev.anton_kulakov.service.CookieService;
import dev.anton_kulakov.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Slf4j
public class IdentifiedUserInterceptor implements HandlerInterceptor {
    private final CookieService cookieService;
    private final SessionService sessionService;

    @Autowired
    public IdentifiedUserInterceptor(CookieService cookieService, SessionService sessionService) {
        this.cookieService = cookieService;
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {

        Optional<Cookie> cookieOptional = cookieService.getByName(request.getCookies(), "uuid");

        if (cookieOptional.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/sign-in");
            return false;
        }

        Optional<UserSession> userSessionOptional = sessionService.get(cookieOptional.get());

        if (userSessionOptional.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/sign-in");
            return false;
        }

        User user;

        try {
            user = userSessionOptional.get().getUser();
        } catch (Exception e) {
            log.error("User from UserSession {} not found", userSessionOptional.get().getId());
            throw new UserNotFoundException("User not found");
        }

        request.setAttribute("user", user);
        return true;
    }
}
