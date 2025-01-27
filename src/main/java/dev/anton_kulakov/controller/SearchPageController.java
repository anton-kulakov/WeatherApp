package dev.anton_kulakov.controller;

import dev.anton_kulakov.dao.LocationDao;
import dev.anton_kulakov.dao.UserDao;
import dev.anton_kulakov.dto.LocationResponseDto;
import dev.anton_kulakov.model.Location;
import dev.anton_kulakov.model.User;
import dev.anton_kulakov.model.UserSession;
import dev.anton_kulakov.service.CookieService;
import dev.anton_kulakov.service.LocationMapper;
import dev.anton_kulakov.service.OpenWeatherAPIService;
import dev.anton_kulakov.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("search")
public class SearchPageController {
    private final LocationDao locationDao;
    private final UserDao userDao;
    private final SessionService sessionService;
    private final CookieService cookieService;
    private final OpenWeatherAPIService openWeatherAPIService;
    private final LocationMapper locationMapper;

    @Autowired
    public SearchPageController(LocationDao locationDao, UserDao userDao, SessionService sessionService, CookieService cookieService, OpenWeatherAPIService openWeatherAPIService, LocationMapper locationMapper) {
        this.locationDao = locationDao;
        this.userDao = userDao;
        this.sessionService = sessionService;
        this.cookieService = cookieService;
        this.openWeatherAPIService = openWeatherAPIService;
        this.locationMapper = locationMapper;
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
            return "index";
        }

        int userID = userSessionOptional.get().getUserID();

        List<LocationResponseDto> locationResponseDtoList;

        try {
            locationResponseDtoList = openWeatherAPIService.getLocationsByName(query, userID);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        Optional<User> userOptional = userDao.getById(userID);

        userOptional.ifPresent(user -> model.addAttribute("login", user.getLogin()));
        model.addAttribute("locationResponseDtoList", locationResponseDtoList);

        return "search-results";
    }

    @PostMapping
    public String doPost(HttpServletRequest request,
                         @ModelAttribute ("locationResponseDto") LocationResponseDto locationResponseDto) {
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = cookieService.findCookieByName(cookies);

        if (cookieOptional.isEmpty()) {
            return "sign-in";
        }

        Optional<UserSession> userSessionOptional = sessionService.get(cookieOptional.get());

        if (userSessionOptional.isEmpty()) {
            return "sign-in";
        }

        int userID = userSessionOptional.get().getUserID();

        Location location = locationMapper.toLocation(locationResponseDto);
        location.setUserID(userID);

        locationDao.persist(location);

        return "search-results";
    }

    @DeleteMapping
    public String doDelete(@RequestParam("latitude") BigDecimal latitude,
                           @RequestParam("longitude") BigDecimal longitude) {
        locationDao.delete(latitude, longitude);

        return "search-results";
    }
}
