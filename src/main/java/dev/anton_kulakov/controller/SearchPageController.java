package dev.anton_kulakov.controller;

import dev.anton_kulakov.dao.LocationDao;
import dev.anton_kulakov.dto.LocationResponseDto;
import dev.anton_kulakov.exception.WeatherApiException;
import dev.anton_kulakov.model.Location;
import dev.anton_kulakov.model.User;
import dev.anton_kulakov.service.LocationMapper;
import dev.anton_kulakov.service.OpenWeatherApiService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/search")
@Slf4j
public class SearchPageController {
    private final LocationDao locationDao;
    private final LocationMapper locationMapper;
    private final OpenWeatherApiService openWeatherApiService;

    @Autowired
    public SearchPageController(LocationDao locationDao, LocationMapper locationMapper, OpenWeatherApiService openWeatherApiService) {
        this.locationDao = locationDao;
        this.locationMapper = locationMapper;
        this.openWeatherApiService = openWeatherApiService;
    }

    @GetMapping
    public String doGet(Model model, HttpServletRequest request) {
        String query = (String) request.getAttribute("queryAttribute");
        User user = (User) request.getAttribute("user");
        List<LocationResponseDto> locationResponseDtoList;

        try {
            locationResponseDtoList = openWeatherApiService.getLocationsByName(query, user);
        } catch (IOException | InterruptedException e) {
            log.error("There is an error on the server");
            throw new WeatherApiException("There is an error on the server");
        }

        model.addAttribute("login", user.getLogin());
        model.addAttribute("query", query);
        model.addAttribute("locationResponseDtoList", locationResponseDtoList);

        return "search-results";
    }

    @PostMapping
    public String doPost(HttpServletRequest request,
                         @ModelAttribute("locationResponseDto") LocationResponseDto locationResponseDto,
                         RedirectAttributes redirectAttributes) {

        String query = (String) request.getAttribute("queryAttribute");
        User user = (User) request.getAttribute("user");
        Location location = locationMapper.toLocation(locationResponseDto);

        location.setUser(user);
        locationDao.persist(user, location);

        redirectAttributes.addAttribute("query", query);
        return "redirect:/search";
    }

    @DeleteMapping
    public String doDelete(HttpServletRequest request,
                           @RequestParam("latitude") BigDecimal latitude,
                           @RequestParam("longitude") BigDecimal longitude,
                           RedirectAttributes redirectAttributes) {

        String query = (String) request.getAttribute("queryAttribute");
        User user = (User) request.getAttribute("user");

        locationDao.delete(user, latitude, longitude);
        redirectAttributes.addAttribute("query", query);

        return "redirect:/search";
    }
}
