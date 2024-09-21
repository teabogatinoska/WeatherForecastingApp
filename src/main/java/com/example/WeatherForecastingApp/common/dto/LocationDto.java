package com.example.WeatherForecastingApp.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDto {
    private String name;
    private String country;

    public LocationDto() {}

    public LocationDto(String name) {
        this.name = name;
    }

    public LocationDto(String name, String country) {
        this.name = name;
        this.country = country;
    }

    @Override
    public String toString() {
        return "LocationDto{" +
                "name='" + name + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
