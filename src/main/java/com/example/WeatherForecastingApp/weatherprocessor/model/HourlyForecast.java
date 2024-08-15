package com.example.WeatherForecastingApp.weatherprocessor.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class HourlyForecast {

    private LocalDateTime timestamp;
    private double temperature;

    public HourlyForecast(LocalDateTime timestamp, double temperature) {
        this.timestamp = timestamp;
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "HourlyForecast{" +
                "timestamp=" + timestamp +
                ", temperature=" + temperature +
                '}';
    }
}
