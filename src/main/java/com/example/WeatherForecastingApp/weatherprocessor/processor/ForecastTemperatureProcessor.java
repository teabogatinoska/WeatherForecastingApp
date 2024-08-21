package com.example.WeatherForecastingApp.weatherprocessor.processor;

import com.example.WeatherForecastingApp.weatherprocessor.model.CombinedDailyForecast;
import com.example.WeatherForecastingApp.weatherprocessor.model.CombinedHourlyForecast;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

public class ForecastTemperatureProcessor implements WeatherDataProcessor {

    @Override
    public Map<LocalDateTime, Integer> calculateHourlyData(Map<LocalDateTime, CombinedHourlyForecast> combinedHourlyForecasts) {
        Map<LocalDateTime, Integer> averageHourlyTemperatures = new TreeMap<>();

        for (Map.Entry<LocalDateTime, CombinedHourlyForecast> entry : combinedHourlyForecasts.entrySet()) {
            LocalDateTime timestamp = entry.getKey();
            CombinedHourlyForecast hourlyForecast = entry.getValue();

            double averageTemperature = hourlyForecast.getTemperatures().stream()
                    .mapToDouble(temp -> Math.round(temp * 10) / 10.0)
                    .average()
                    .orElse(0.0);
            int roundedAverageTemperature = (int) Math.round(averageTemperature);
            averageHourlyTemperatures.put(timestamp, roundedAverageTemperature);
        }
        return averageHourlyTemperatures;
    }

    @Override
    public Map<LocalDate, Integer> calculateDailyData(Map<LocalDate, CombinedDailyForecast> combinedDailyForecasts) {
        Map<LocalDate, Integer> averageDailyTemperatures = new TreeMap<>();

        for (Map.Entry<LocalDate, CombinedDailyForecast> entry : combinedDailyForecasts.entrySet()) {
            LocalDate date = entry.getKey();
            CombinedDailyForecast dailyForecast = entry.getValue();

            double dailyAverageTemperature = dailyForecast.getHourlyForecasts().stream()
                    .mapToDouble(hourlyForecast -> hourlyForecast.getTemperatures().stream()
                            .mapToDouble(temp -> Math.round(temp * 10) / 10.0)
                            .average()
                            .orElse(0.0))
                    .map(Math::round)
                    .average()
                    .orElse(0.0);
            int roundedDailyAverageTemperature = (int) Math.round(dailyAverageTemperature);
            averageDailyTemperatures.put(date, roundedDailyAverageTemperature);
        }

        return averageDailyTemperatures;
    }
}
