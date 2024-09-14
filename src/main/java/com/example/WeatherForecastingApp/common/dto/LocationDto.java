package com.example.WeatherForecastingApp.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDto {
    private String name;
    private double latitude;
    private double longitude;
    private String country;

    public LocationDto() {}

    public LocationDto(String name) {
        this.name = name;
    }

    public LocationDto(String name, double latitude, double longitude, String country) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
    }
}
