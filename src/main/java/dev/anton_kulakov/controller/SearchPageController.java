package dev.anton_kulakov.controller;

import dev.anton_kulakov.dao.UserDao;
import dev.anton_kulakov.dto.LocationResponseDto;
import dev.anton_kulakov.model.User;
import dev.anton_kulakov.model.UserSession;
import dev.anton_kulakov.service.CookieService;
import dev.anton_kulakov.service.OpenWeatherAPIService;
import dev.anton_kulakov.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("search")
public class SearchPageController {
    private final UserDao userDao;
    private final SessionService sessionService;
    private final CookieService cookieService;
    private final OpenWeatherAPIService openWeatherAPIService;

    @Autowired
    public SearchPageController(UserDao userDao, SessionService sessionService, CookieService cookieService, OpenWeatherAPIService openWeatherAPIService) {
        this.userDao = userDao;
        this.sessionService = sessionService;
        this.cookieService = cookieService;
        this.openWeatherAPIService = openWeatherAPIService;
    }

    @GetMapping
    public String doGet(Model model, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = cookieService.findCookieByName(cookies);

        if (cookieOptional.isEmpty()) {
            return "sign-in";
        }

        Optional<UserSession> userSessionOptional = sessionService.get(cookieOptional.get());

        if (userSessionOptional.isEmpty()) {
            return "sign-in";
        }

        String query = request.getParameter("query");

        if (query == null) {
            return "search-results";
        }

        int userID = userSessionOptional.get().getUserID();

        List<LocationResponseDto> locationResponseDtoList;

        try {
            locationResponseDtoList = openWeatherAPIService.getLocationsByName(query);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        Optional<User> userOptional = userDao.getById(userID);

        userOptional.ifPresent(user -> model.addAttribute("login", user.getLogin()));
        model.addAttribute("locationResponseDtoList", locationResponseDtoList);

        return "search-results";
    }
}
