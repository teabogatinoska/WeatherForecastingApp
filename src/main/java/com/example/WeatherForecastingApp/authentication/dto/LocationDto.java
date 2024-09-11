package com.example.WeatherForecastingApp.authentication.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDto {
    private String name;
    private double latitude;
    private double longitude;
    private String country;
}
