package com.example.weatherprocessor.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WeatherData {
    private String source;
    private List<HourlyForecast> hourlyForecasts;

    public WeatherData(String source, List<HourlyForecast> hourlyForecasts) {
        this.source = source;
        this.hourlyForecasts = hourlyForecasts;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "source='" + source + '\'' +
                ", hourlyForecasts=" + hourlyForecasts.toString() +
                '}';
    }
}
