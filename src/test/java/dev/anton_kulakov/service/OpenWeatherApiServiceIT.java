package dev.anton_kulakov.service;

import dev.anton_kulakov.config.TestConfig;
import dev.anton_kulakov.dto.WeatherResponseDto;
import dev.anton_kulakov.exception.WeatherApiException;
import dev.anton_kulakov.model.Location;
import dev.anton_kulakov.model.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
@SpringJUnitConfig(classes = TestConfig.class)
@WebAppConfiguration
@ExtendWith(MockitoExtension.class)
public class OpenWeatherApiServiceIT {
    @Autowired
    private OpenWeatherApiService openWeatherApiService;

    @MockitoBean
    private HttpClient httpClient;

    @SneakyThrows
    @Test
    @DisplayName("Should provide the data according to the request")
    void shouldProvideDataAccordingToRequest() {
        String body = "{\"coord\":{\"lon\":37.6171,\"lat\":55.7483},\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04n\"}],\"base\":\"stations\",\"main\":{\"temp\":-2.42,\"feels_like\":-8.84,\"temp_min\":-2.66,\"temp_max\":-1.42,\"pressure\":1025,\"humidity\":50,\"sea_level\":1025,\"grnd_level\":1005},\"visibility\":10000,\"wind\":{\"speed\":6.62,\"deg\":187,\"gust\":13.89},\"clouds\":{\"all\":100},\"dt\":1737569273,\"sys\":{\"type\":2,\"id\":2094500,\"country\":\"RU\",\"sunrise\":1737524412,\"sunset\":1737553299},\"timezone\":10800,\"id\":524901,\"name\":\"Moscow\",\"cod\":200}";

        HttpResponse<String> httpResponseMock = Mockito.mock(HttpResponse.class);
        Mockito.when(httpResponseMock.body()).thenReturn(body);
        Mockito.when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);

        ArrayList<Location> locations = getLocationList();
        List<WeatherResponseDto> weatherResponseDtoList = openWeatherApiService.getLocationsByCoordinates(locations);

        Assertions.assertEquals("Overcast clouds", weatherResponseDtoList.get(0).getWeather()[0].getDescription());
    }

    @SneakyThrows
    @Test
    @DisplayName("Should throw an exception when trying to get a list of locations based on coordinates")
    void shouldThrowAnExceptionWhenGettingLocationsByCoordinates() {
        HttpResponse<String> httpResponseMock = Mockito.mock(HttpResponse.class);
        Mockito.when(httpResponseMock.statusCode()).thenReturn(404);
        Mockito.when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);

        ArrayList<Location> locations = getLocationList();

        Assertions.assertThrows(WeatherApiException.class, () -> openWeatherApiService.getLocationsByCoordinates(locations));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should throw an exception when trying to get a list of locations based on name")
    void shouldThrowAnExceptionWhenGettingLocationsByName() {
        HttpResponse<String> httpResponseMock = Mockito.mock(HttpResponse.class);
        Mockito.when(httpResponseMock.statusCode()).thenReturn(404);
        Mockito.when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);
        User user = new User();

        Assertions.assertThrows(WeatherApiException.class, () -> openWeatherApiService.getLocationsByName("Moscow", user));
    }

    private static ArrayList<Location> getLocationList() {
        Location location = new Location();
        location.setLatitude(BigDecimal.valueOf(55.7522));
        location.setLongitude(BigDecimal.valueOf(37.6156));
        ArrayList<Location> locations = new ArrayList<>();
        locations.add(location);
        return locations;
    }
}
