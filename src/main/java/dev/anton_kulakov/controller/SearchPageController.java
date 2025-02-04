package dev.anton_kulakov.controller;

import dev.anton_kulakov.dao.LocationDao;
import dev.anton_kulakov.dto.LocationResponseDto;
import dev.anton_kulakov.dto.UserRequestDto;
import dev.anton_kulakov.model.Location;
import dev.anton_kulakov.service.LocationMapper;
import dev.anton_kulakov.service.OpenWeatherAPIService;
import jakarta.servlet.http.HttpServletRequest;
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
public class SearchPageController {
    private final LocationDao locationDao;
    private final LocationMapper locationMapper;
    private final OpenWeatherAPIService openWeatherAPIService;

    @Autowired
    public SearchPageController(LocationDao locationDao, LocationMapper locationMapper, OpenWeatherAPIService openWeatherAPIService) {
        this.locationDao = locationDao;
        this.locationMapper = locationMapper;
        this.openWeatherAPIService = openWeatherAPIService;
    }

    @GetMapping
    public String doGet(Model model, HttpServletRequest request) {
        String query = (String) request.getAttribute("queryAttribute");

        UserRequestDto userRequestDto = (UserRequestDto) request.getAttribute("userRequestDto");
        int userId = userRequestDto.getId();
        List<LocationResponseDto> locationResponseDtoList;

        try {
            locationResponseDtoList = openWeatherAPIService.getLocationsByName(query, userId);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        model.addAttribute("login", userRequestDto.getLogin());
        model.addAttribute("query", query);
        model.addAttribute("locationResponseDtoList", locationResponseDtoList);

        return "search-results";
    }

    @PostMapping
    public String doPost(HttpServletRequest request,
                         @ModelAttribute("locationResponseDto") LocationResponseDto locationResponseDto,
                         RedirectAttributes redirectAttributes) {

        String query = (String) request.getAttribute("queryAttribute");

        UserRequestDto userRequestDto = (UserRequestDto) request.getAttribute("userRequestDto");

        Location location = locationMapper.toLocation(locationResponseDto);
        location.setUserID(userRequestDto.getId());

        locationDao.persist(location);

        redirectAttributes.addAttribute("query", query);
        return "redirect:/search";
    }

    @DeleteMapping
    public String doDelete(HttpServletRequest request,
                           @RequestParam("latitude") BigDecimal latitude,
                           @RequestParam("longitude") BigDecimal longitude,
                           RedirectAttributes redirectAttributes) {

        String query = (String) request.getAttribute("queryAttribute");

        locationDao.delete(latitude, longitude);
        redirectAttributes.addAttribute("query", query);

        return "redirect:/search";
    }
}
