package com.example.WeatherForecastingApp.weatherprocessor.processor;

import com.example.WeatherForecastingApp.weatherprocessor.model.CombinedDailyForecast;
import com.example.WeatherForecastingApp.weatherprocessor.model.CombinedHourlyForecast;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

public class ForecastWindProcessor implements WeatherDataProcessor{

    @Override
    public Map<LocalDateTime, Integer> calculateHourlyData(Map<LocalDateTime, CombinedHourlyForecast> combinedHourlyForecasts) {
        Map<LocalDateTime, Integer> averageHourlyWindSpeed = new TreeMap<>();

        for (Map.Entry<LocalDateTime, CombinedHourlyForecast> entry : combinedHourlyForecasts.entrySet()) {
            LocalDateTime timestamp = entry.getKey();
            CombinedHourlyForecast hourlyForecast = entry.getValue();

            double averageWindSpeed = hourlyForecast.getWindSpeeds().stream()
                    .mapToDouble(temp -> Math.round(temp * 10) / 10.0)
                    .average()
                    .orElse(0.0);
            int roundedAverageWindSpeed = (int) Math.round(averageWindSpeed);
            averageHourlyWindSpeed.put(timestamp, roundedAverageWindSpeed);
        }
        return averageHourlyWindSpeed;
    }

    @Override
    public Map<LocalDate, Integer> calculateDailyData(Map<LocalDate, CombinedDailyForecast> combinedDailyForecasts) {
        Map<LocalDate, Integer> averageDailyWindSpeed = new TreeMap<>();

        for (Map.Entry<LocalDate, CombinedDailyForecast> entry : combinedDailyForecasts.entrySet()) {
            LocalDate date = entry.getKey();
            CombinedDailyForecast dailyForecast = entry.getValue();

            double averageWindSpeed = dailyForecast.getHourlyForecasts().stream()
                    .flatMap(f -> f.getWindSpeeds().stream())
                    .mapToDouble(temp -> Math.round(temp * 10) / 10.0)
                    .average()
                    .orElse(0.0);

            int roundedDailyAverageWindSpeed = (int) Math.round(averageWindSpeed);
            averageDailyWindSpeed.put(date, roundedDailyAverageWindSpeed);
        }

        return averageDailyWindSpeed;
    }
}
