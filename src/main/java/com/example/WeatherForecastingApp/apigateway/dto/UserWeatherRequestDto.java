package com.example.WeatherForecastingApp.apigateway.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWeatherRequestDto {

    private String username;
    private String location;

    public UserWeatherRequestDto(String username, String location) {
        this.username = username;
        this.location = location;
    }
}
