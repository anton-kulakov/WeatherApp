package dev.anton_kulakov.service;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
public class CookieService {
    public Optional<Cookie> getCookieByName(Cookie[] cookies, String uuid) {
        return cookies != null ? Arrays.stream(cookies)
                .filter(cookie -> uuid.equals(cookie.getName()))
                .findFirst() : Optional.empty();
    }

    public Cookie createCookie(UUID uuid) {
        Cookie cookie = new Cookie("uuid", uuid.toString());
        int secondsInADay = 24 * 60 * 60;
        cookie.setMaxAge(secondsInADay);
        cookie.setHttpOnly(true);

        return cookie;
    }
}
