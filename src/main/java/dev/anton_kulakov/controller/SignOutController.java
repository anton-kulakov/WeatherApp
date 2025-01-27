package dev.anton_kulakov.controller;

import dev.anton_kulakov.service.CookieService;
import dev.anton_kulakov.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("sign-out")
public class SignOutController {
    private final CookieService cookieService;
    private final SessionService sessionService;

    @Autowired
    public SignOutController(CookieService cookieService, SessionService sessionService) {
        this.cookieService = cookieService;
        this.sessionService = sessionService;
    }

    @PostMapping
    public String doPost(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        Optional<Cookie> foundCookieOptional = cookieService.findCookieByUuidName(cookies);

        foundCookieOptional.ifPresent(sessionService::deleteByID);

        return "sign-in";
    }
}
