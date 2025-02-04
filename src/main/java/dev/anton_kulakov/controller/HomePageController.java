package dev.anton_kulakov.controller;

import dev.anton_kulakov.dao.LocationDao;
import dev.anton_kulakov.dto.UserRequestDto;
import dev.anton_kulakov.dto.WeatherResponseDto;
import dev.anton_kulakov.model.Location;
import dev.anton_kulakov.service.OpenWeatherAPIService;
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

@Controller
@RequestMapping("/index")
public class HomePageController {
    private final LocationDao locationDao;
    private final OpenWeatherAPIService openWeatherAPIService;

    @Autowired
    public HomePageController(LocationDao locationDao, OpenWeatherAPIService openWeatherAPIService) {
        this.locationDao = locationDao;
        this.openWeatherAPIService = openWeatherAPIService;
    }

    @GetMapping
    public String doGet(Model model, HttpServletRequest request) {
        UserRequestDto userRequestDto = (UserRequestDto) request.getAttribute("userRequestDto");
        List<Location> locations = locationDao.getByUserId(userRequestDto.getId());
        List<WeatherResponseDto> weatherResponseDtoList;

        try {
            weatherResponseDtoList = openWeatherAPIService.getLocationByCoordinates(locations);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        model.addAttribute("login", userRequestDto.getLogin());
        model.addAttribute("weatherResponseDtoList", weatherResponseDtoList);

        return "index";
    }

    @DeleteMapping
    public String doDelete(@RequestParam("latitude") BigDecimal latitude,
                           @RequestParam("longitude") BigDecimal longitude) {
        locationDao.delete(latitude, longitude);
        return "redirect:/index";
    }
}
