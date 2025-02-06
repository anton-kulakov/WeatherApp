package dev.anton_kulakov.controller.interceptor;

import dev.anton_kulakov.model.UserSession;
import dev.anton_kulakov.service.CookieService;
import dev.anton_kulakov.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

public class UnidentifiedUserInterceptor implements HandlerInterceptor {
    private final CookieService cookieService;
    private final SessionService sessionService;

    @Autowired
    public UnidentifiedUserInterceptor(CookieService cookieService, SessionService sessionService) {
        this.cookieService = cookieService;
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        Optional<Cookie> cookieOptional = cookieService.getByName(request.getCookies(), "uuid");
        Optional<UserSession> userSessionOptional = Optional.empty();

        if (cookieOptional.isPresent()) {
            userSessionOptional = sessionService.get(cookieOptional.get());
        }

        if (userSessionOptional.isPresent()) {
            response.sendRedirect(request.getContextPath() + "/index");
            return false;
        }

        return true;
    }
}
