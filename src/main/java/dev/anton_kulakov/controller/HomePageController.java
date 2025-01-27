package dev.anton_kulakov.controller;

import dev.anton_kulakov.dao.LocationDao;
import dev.anton_kulakov.dao.UserDao;
import dev.anton_kulakov.dto.WeatherResponseDto;
import dev.anton_kulakov.model.Location;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("index")
public class HomePageController {
    private final LocationDao locationDao;
    private final UserDao userDao;
    private final SessionService sessionService;
    private final CookieService cookieService;
    private final OpenWeatherAPIService openWeatherAPIService;

    @Autowired
    public HomePageController(LocationDao locationDao, UserDao userDao, SessionService sessionService, CookieService cookieService, OpenWeatherAPIService openWeatherAPIService) {
        this.locationDao = locationDao;
        this.userDao = userDao;
        this.sessionService = sessionService;
        this.cookieService = cookieService;
        this.openWeatherAPIService = openWeatherAPIService;
    }
    @GetMapping
    public String doGet(Model model, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = cookieService.findCookieByUuidName(cookies);

        if (cookieOptional.isEmpty()) {
            return "sign-in";
        }

        Optional<UserSession> userSessionOptional = sessionService.get(cookieOptional.get());

        if (userSessionOptional.isEmpty()) {
            return "sign-in";
        }

        int userID = userSessionOptional.get().getUserID();
        List<Location> locations = locationDao.getByUserId(userID);

        List<WeatherResponseDto> weatherResponseDtoList;

        try {
            weatherResponseDtoList = openWeatherAPIService.getLocationByCoordinates(locations);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        Optional<User> userOptional = userDao.getById(userID);

        userOptional.ifPresent(user -> model.addAttribute("login", user.getLogin()));
        model.addAttribute("weatherResponseDtoList", weatherResponseDtoList);

        return "index";
    }

    @DeleteMapping
    public String doDelete(@RequestParam("latitude") BigDecimal latitude,
                           @RequestParam("longitude") BigDecimal longitude) {
        locationDao.delete(latitude, longitude);

        return "index";
    }
}
