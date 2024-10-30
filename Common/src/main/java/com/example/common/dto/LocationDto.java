package com.example.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationDto {
    private Long id;
    private String name;
    private String country;
    private Double longitude;
    private Double latitude;

    public LocationDto() {}

    public LocationDto(String name, String country, Double latitude, Double longitude) {
        this.name = name;
        this.country = country;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "LocationDto{" +
                "name='" + name + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
