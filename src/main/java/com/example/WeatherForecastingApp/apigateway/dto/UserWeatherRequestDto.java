package com.example.WeatherForecastingApp.apigateway.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWeatherRequestDto {

    private Long userId;
    private String username;
    private Double latitude;
    private Double longitude;

    public UserWeatherRequestDto() {
    }

    @JsonCreator
    public UserWeatherRequestDto(@JsonProperty("username") String username, @JsonProperty("latitude") Double latitude, @JsonProperty("longitude") Double longitude) {
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "UserWeatherRequestDto{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
