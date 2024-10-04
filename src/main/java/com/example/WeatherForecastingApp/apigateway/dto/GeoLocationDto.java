package com.example.WeatherForecastingApp.apigateway.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeoLocationDto {
    private Long id;
    private String name;
    private String country;
    private Double longitude;
    private Double latitude;
    private String area;

    public GeoLocationDto() {}
    public GeoLocationDto(String name, String country, Double latitude, Double longitude, String area) {
        this.name = name;
        this.country = country;
        this.longitude = longitude;
        this.latitude = latitude;
        this.area = area;
    }

    public GeoLocationDto(Long id, String name, String country, Double longitude, Double latitude, String area) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.longitude = longitude;
        this.latitude = latitude;
        this.area = area;
    }


}
