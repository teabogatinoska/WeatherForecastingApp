package com.example.WeatherForecastingApp.weatherprocessor.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CombinedHourlyForecast {
    private final LocalDateTime timestamp;
    private final List<Double> temperatures = new ArrayList<>();

    public CombinedHourlyForecast(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public CombinedHourlyForecast(LocalDateTime timestamp, double temperature) {
        this.timestamp = timestamp;
        this.temperatures.add(temperature);
    }

    public void addTemperature(double temperature) {
        temperatures.add(temperature);
    }

    @Override
    public String toString() {
        return "CombinedHourlyForecast{" +
                "timestamp=" + timestamp +
                ", temperatures=" + temperatures +
                '}';
    }
}
