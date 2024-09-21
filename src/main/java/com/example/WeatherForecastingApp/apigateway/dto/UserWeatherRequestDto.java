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
    private String location;
    private String country;

    public UserWeatherRequestDto() {
    }

    @JsonCreator
    public UserWeatherRequestDto(@JsonProperty("username") String username, @JsonProperty("location") String location, @JsonProperty("country") String country) {
        this.username = username;
        this.location = location;
        this.country = country;
    }

    @Override
    public String toString() {
        return "UserWeatherRequestDto{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", location='" + location + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
