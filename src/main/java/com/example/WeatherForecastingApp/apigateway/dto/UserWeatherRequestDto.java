package com.example.WeatherForecastingApp.apigateway.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWeatherRequestDto {

    private String username;
    private String location;

    public UserWeatherRequestDto() {
    }

    @JsonCreator
    public UserWeatherRequestDto(@JsonProperty("username") String username, @JsonProperty("location") String location) {
        this.username = username;
        this.location = location;
    }

}
