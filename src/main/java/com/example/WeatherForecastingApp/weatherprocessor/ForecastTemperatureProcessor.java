package com.example.WeatherForecastingApp.weatherprocessor;

import com.example.WeatherForecastingApp.weatherprocessor.model.CombinedDailyForecast;
import com.example.WeatherForecastingApp.weatherprocessor.model.CombinedHourlyForecast;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

public class ForecastTemperatureProcessor {

    private final WeatherDataAggregator weatherDataAggregator;

    public ForecastTemperatureProcessor(WeatherDataAggregator weatherDataAggregator) {
        this.weatherDataAggregator = weatherDataAggregator;
    }

    public Map<LocalDateTime, Double> calculateAverageHourlyTemperature() {
        Map<LocalDateTime, CombinedHourlyForecast> combinedHourlyForecasts = weatherDataAggregator.getCombinedHourlyForecasts();
        Map<LocalDateTime, Double> averageHourlyTemperatures = new TreeMap<>();

        for (Map.Entry<LocalDateTime, CombinedHourlyForecast> entry : combinedHourlyForecasts.entrySet()) {
            LocalDateTime timestamp = entry.getKey();
            CombinedHourlyForecast hourlyForecast = entry.getValue();

            double averageTemperature = hourlyForecast.getTemperatures().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);

            averageHourlyTemperatures.put(timestamp, averageTemperature);
        }
        return averageHourlyTemperatures;
    }

    public Map<LocalDate, Double> calculateAverageDailyTemperatures() {
        Map<LocalDate, CombinedDailyForecast> combinedDailyForecasts = weatherDataAggregator.getCombinedDailyForecasts();
        Map<LocalDate, Double> averageDailyTemperatures = new TreeMap<>();

        for (Map.Entry<LocalDate, CombinedDailyForecast> entry : combinedDailyForecasts.entrySet()) {
            LocalDate date = entry.getKey();
            CombinedDailyForecast dailyForecast = entry.getValue();

            double averageTemperature = dailyForecast.getHourlyForecasts().stream()
                    .flatMap(f -> f.getTemperatures().stream())
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);

            averageDailyTemperatures.put(date, averageTemperature);
        }

        return averageDailyTemperatures;
    }

}
