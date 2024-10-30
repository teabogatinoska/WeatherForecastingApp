package com.example.weatherprocessor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class HourlyForecast {

    private LocalDateTime timestamp;
    private double temperature;
    private double humidity;
    private double precipitationProbability;
    private double windSpeed;
    private String description;

    public HourlyForecast(LocalDateTime timestamp, double temperature, double humidity, double precipitationProbability, double windSpeed, String description) {
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipitationProbability = precipitationProbability;
        this.windSpeed = windSpeed;
        this.description = description;
    }

    public HourlyForecast(LocalDateTime timestamp, double temperature, double humidity, double precipitationProbability, double windSpeed) {
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipitationProbability = precipitationProbability;
        this.windSpeed = windSpeed;
        this.description = null;
    }

    @Override
    public String toString() {
        return "HourlyForecast{" +
                "timestamp=" + timestamp +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", precipitationProbability=" + precipitationProbability +
                ", windSpeed=" + windSpeed +
                ", description='" + description + '\'' +
                '}';
    }
}
