package com.example.weatherprocessor.model;

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
    private final List<Double> humidities = new ArrayList<>();
    private final List<Double> precipitationProbabilities = new ArrayList<>();
    private final List<Double> windSpeeds = new ArrayList<>();
    private String description;

    public CombinedHourlyForecast(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void addTemperature(double temperature) {
        temperatures.add(temperature);
    }

    public void addHumidity(double humidity) {
        humidities.add(humidity);
    }

    public void addPrecipitationProbability(double probability) {
        precipitationProbabilities.add(probability);
    }

    public void addWindSpeed(double windSpeed) {
        windSpeeds.add(windSpeed);
    }


    @Override
    public String toString() {
        return "CombinedHourlyForecast{" +
                "timestamp=" + timestamp +
                ", temperatures=" + temperatures +
                ", humidities=" + humidities +
                ", precipitationProbabilities=" + precipitationProbabilities +
                ", windSpeeds=" + windSpeeds +
                ", description=" + description +
                '}';
    }
}
