package com.example.WeatherForecastingApp.weatherprocessor.processor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

import com.example.WeatherForecastingApp.weatherprocessor.model.CombinedDailyForecast;
import com.example.WeatherForecastingApp.weatherprocessor.model.CombinedHourlyForecast;

public class ForecastPrecipitationProcessor implements WeatherDataProcessor{

    @Override
    public Map<LocalDateTime, Integer> calculateHourlyData(Map<LocalDateTime, CombinedHourlyForecast> combinedHourlyForecasts) {
        Map<LocalDateTime, Integer> averageHourlyPrecipitation = new TreeMap<>();

        for (Map.Entry<LocalDateTime, CombinedHourlyForecast> entry : combinedHourlyForecasts.entrySet()) {
            LocalDateTime timestamp = entry.getKey();
            CombinedHourlyForecast hourlyForecast = entry.getValue();

            double averagePrecipitation = hourlyForecast.getPrecipitationProbabilities().stream()
                    .mapToDouble(temp -> Math.round(temp * 10) / 10.0)
                    .average()
                    .orElse(0.0);
            int roundedAveragePrecipitation = (int) Math.round(averagePrecipitation);
            averageHourlyPrecipitation.put(timestamp, roundedAveragePrecipitation);
        }
        return averageHourlyPrecipitation;
    }

    @Override
    public Map<LocalDate, Integer> calculateDailyData(Map<LocalDate, CombinedDailyForecast> combinedDailyForecasts) {
        Map<LocalDate, Integer> averageDailyPrecipitation = new TreeMap<>();

        for (Map.Entry<LocalDate, CombinedDailyForecast> entry : combinedDailyForecasts.entrySet()) {
            LocalDate date = entry.getKey();
            CombinedDailyForecast dailyForecast = entry.getValue();

            double averagePrecipitation = dailyForecast.getHourlyForecasts().stream()
                    .flatMap(f -> f.getPrecipitationProbabilities().stream())
                    .mapToDouble(temp -> Math.round(temp * 10) / 10.0)
                    .average()
                    .orElse(0.0);

            int roundedDailyAveragePrecipitation = (int) Math.round(averagePrecipitation);
            averageDailyPrecipitation.put(date, roundedDailyAveragePrecipitation);
        }

        return averageDailyPrecipitation;
    }
}
