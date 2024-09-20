package com.example.WeatherForecastingApp.weatheralert.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Location {
    private String name;
    private double latitude;
    private double longitude;
}
