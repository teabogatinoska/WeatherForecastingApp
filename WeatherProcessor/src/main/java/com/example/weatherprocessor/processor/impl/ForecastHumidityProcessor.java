package com.example.weatherprocessor.processor.impl;

import com.example.weatherprocessor.model.CombinedDailyForecast;
import com.example.weatherprocessor.model.CombinedHourlyForecast;
import com.example.weatherprocessor.processor.DailyAverageDataProcessor;
import com.example.weatherprocessor.processor.HourlyDataProcessor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

public class ForecastHumidityProcessor implements HourlyDataProcessor, DailyAverageDataProcessor {


    @Override
    public Map<LocalDateTime, Integer> calculateHourlyData(Map<LocalDateTime, CombinedHourlyForecast> combinedHourlyForecasts) {
        Map<LocalDateTime, Integer> averageHourlyHumidity = new TreeMap<>();

        for (Map.Entry<LocalDateTime, CombinedHourlyForecast> entry : combinedHourlyForecasts.entrySet()) {
            LocalDateTime timestamp = entry.getKey();
            CombinedHourlyForecast hourlyForecast = entry.getValue();

            double averageHumidity = hourlyForecast.getHumidities().stream()
                    .mapToDouble(temp -> Math.round(temp * 10) / 10.0)
                    .average()
                    .orElse(0.0);
            int roundedAverageHumidity = (int) Math.round(averageHumidity);
            averageHourlyHumidity.put(timestamp, roundedAverageHumidity);
        }
        return averageHourlyHumidity;
    }

    @Override
    public Map<LocalDate,  Integer> calculateDailyData(Map<LocalDate, CombinedDailyForecast> combinedDailyForecasts) {
        Map<LocalDate, Integer> averageDailyHumidity = new TreeMap<>();

        for (Map.Entry<LocalDate, CombinedDailyForecast> entry : combinedDailyForecasts.entrySet()) {
            LocalDate date = entry.getKey();
            CombinedDailyForecast dailyForecast = entry.getValue();

            double averageHumidity = dailyForecast.getHourlyForecasts().stream()
                    .flatMap(f -> f.getHumidities().stream())
                    .mapToDouble(temp -> Math.round(temp * 10) / 10.0)
                    .average()
                    .orElse(0.0);
            int roundedDailyAverageHumidity = (int) Math.round(averageHumidity);
            averageDailyHumidity.put(date, roundedDailyAverageHumidity);
        }

        return averageDailyHumidity;
    }
}

