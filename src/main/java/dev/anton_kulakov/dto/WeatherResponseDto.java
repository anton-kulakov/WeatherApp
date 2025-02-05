package dev.anton_kulakov.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponseDto {
    private String locationName;
    private BigDecimal latitude;
    private BigDecimal longitude;

    @JsonProperty("main")
    private WeatherInfo weatherInfo;

    @JsonProperty("weather")
    private Weather[] weather;

    @JsonProperty("sys")
    private Sys sys;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherInfo {
        @JsonProperty("temp")
        private int temperature;

        @JsonProperty("feels_like")
        private int feelsLikeTemperature;

        @JsonProperty("humidity")
        private int humidity;

        @JsonSetter("temp")
        public void setTemperature(double temp) {
            this.temperature = (int) Math.round(temp);
        }

        @JsonSetter("feels_like")
        public void setFeelsLikeTemperature(double feelsLike) {
            this.feelsLikeTemperature = (int) Math.round(feelsLike);
        }
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        @JsonProperty("description")
        private String description;

        @JsonProperty("icon")
        private String icon;

        @JsonSetter("description")
        public void setDescription(String description) {
            this.description = description.substring(0, 1).toUpperCase() + description.substring(1);
        }
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sys {
        @JsonProperty("country")
        private String country;
    }
}
