package dev.anton_kulakov.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.anton_kulakov.dao.LocationDao;
import dev.anton_kulakov.dto.LocationResponseDto;
import dev.anton_kulakov.dto.WeatherResponseDto;
import dev.anton_kulakov.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class OpenWeatherAPIService {
    @Value("${api.key}")
    private String API_KEY;
    private final static String GET_LOCATION_BY_COORDINATES_URL = "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s&units=metric";
    private final static String GET_LOCATIONS_BY_NAME_URL = "http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=10&appid=%s";
    private final LocationDao locationDao;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public OpenWeatherAPIService(LocationDao locationDao, HttpClient httpClient, ObjectMapper objectMapper) {
        this.locationDao = locationDao;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void checkApiKey() {
        if (API_KEY == null || API_KEY.isBlank()) {
            throw new IllegalArgumentException("API key not configured!");
        }
    }

    public List<WeatherResponseDto> getLocationByCoordinates(List<Location> locations) throws IOException, InterruptedException {
        List<WeatherResponseDto> weatherResponseDtoList = new ArrayList<>();

        for (Location location : locations) {
            WeatherResponseDto weatherResponseDto = getWeatherResponseDto(location);
            weatherResponseDtoList.add(weatherResponseDto);
        }

        return weatherResponseDtoList;
    }

    private WeatherResponseDto getWeatherResponseDto(Location location) throws IOException, InterruptedException {
        BigDecimal latitude = location.getLatitude();
        BigDecimal longitude = location.getLongitude();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GET_LOCATION_BY_COORDINATES_URL.formatted(latitude, longitude, API_KEY)))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("There is an error on the server: " + response.statusCode());
        }

        WeatherResponseDto weatherResponseDto = objectMapper.readValue(response.body(), WeatherResponseDto.class);
        weatherResponseDto.setLatitude(latitude);
        weatherResponseDto.setLongitude(longitude);

        return weatherResponseDto;
    }

    public List<LocationResponseDto> getLocationsByName(String locationName, int userID) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GET_LOCATIONS_BY_NAME_URL.formatted(locationName, API_KEY)))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("There is an error on the server: " + response.statusCode());
        }

        List<LocationResponseDto> locationResponseDtoList = objectMapper.readValue(response.body(), objectMapper.getTypeFactory().constructCollectionType(List.class, LocationResponseDto.class));
        checkIfLocationAddedByUser(locationResponseDtoList, userID);

        return locationResponseDtoList;
    }

    private void checkIfLocationAddedByUser(List<LocationResponseDto> locationResponseDtoList, int userID) {
        for (LocationResponseDto location : locationResponseDtoList) {
            BigDecimal latitude = location.getLatitude();
            BigDecimal longitude = location.getLongitude();

            Long count = locationDao.countByUserId(userID, latitude, longitude);

            if (count == 1) {
                location.setAddedToDatabase(true);
            }
        }
    }
}
