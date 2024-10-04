package com.example.WeatherForecastingApp.weatheralert.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationAlerts {
    private Location location;
    private List<WeatherAlert> alerts;
}