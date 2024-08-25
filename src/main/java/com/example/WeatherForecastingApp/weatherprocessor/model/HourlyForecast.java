package com.example.WeatherForecastingApp.weatherprocessor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class HourlyForecast {

    private LocalDateTime timestamp;
    private double temperature;
    private double humidity;
    private double precipitationProbability;
    private double windSpeed;

    public HourlyForecast(LocalDateTime timestamp, double temperature) {
        this.timestamp = timestamp;
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "HourlyForecast{" +
                "timestamp=" + timestamp +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", precipitationProbability=" + precipitationProbability +
                ", windSpeed=" + windSpeed +
                '}';
    }
}
