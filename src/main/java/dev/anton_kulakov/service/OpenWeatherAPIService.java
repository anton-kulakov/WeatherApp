package dev.anton_kulakov.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.anton_kulakov.dto.WeatherResponseDto;
import dev.anton_kulakov.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private final static String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=fb21f5dcd66367f5ced328ae7af45ac8&units=metric";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public OpenWeatherAPIService(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public List<WeatherResponseDto> getAll(List<Location> locations) throws IOException, InterruptedException {
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
                .uri(URI.create(BASE_URL.formatted(latitude, longitude)))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), WeatherResponseDto.class);
    }
}
