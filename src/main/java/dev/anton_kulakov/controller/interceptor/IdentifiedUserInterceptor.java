package dev.anton_kulakov.controller.interceptor;

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
                             HttpServletResponse response,
                             Object handler) throws Exception {

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

        User user = userSessionOptional.get().getUser();
        request.setAttribute("user", user);

        return true;
    }
}
