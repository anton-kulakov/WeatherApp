package dev.anton_kulakov.controller;

import dev.anton_kulakov.dao.LocationDao;
import dev.anton_kulakov.dto.WeatherResponseDto;
import dev.anton_kulakov.exception.WeatherApiException;
import dev.anton_kulakov.model.Location;
import dev.anton_kulakov.model.User;
import dev.anton_kulakov.service.OpenWeatherApiService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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

@Controller
@RequestMapping(value = {"/", "/index"})
@Slf4j
public class HomePageController {
    private final LocationDao locationDao;
    private final OpenWeatherApiService openWeatherAPIService;

    @Autowired
    public HomePageController(LocationDao locationDao, OpenWeatherApiService openWeatherApiService) {
        this.locationDao = locationDao;
        this.openWeatherAPIService = openWeatherApiService;
    }

    @GetMapping
    public String doGet(Model model, HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        List<Location> userLocations = locationDao.get(user);
        List<WeatherResponseDto> weatherResponseDtoList;

        try {
            weatherResponseDtoList = openWeatherAPIService.getLocationsByCoordinates(userLocations);
        } catch (IOException | InterruptedException e) {
            log.error("There is an error on the server");
            throw new WeatherApiException("There is an error on the server");
        }

        model.addAttribute("login", user.getLogin());
        model.addAttribute("weatherResponseDtoList", weatherResponseDtoList);

        return "index";
    }

    @DeleteMapping
    public String doDelete(HttpServletRequest request,
                           @RequestParam("latitude") BigDecimal latitude,
                           @RequestParam("longitude") BigDecimal longitude) {

        User user = (User) request.getAttribute("user");
        locationDao.delete(user, latitude, longitude);
        return "redirect:/index";
    }
}
