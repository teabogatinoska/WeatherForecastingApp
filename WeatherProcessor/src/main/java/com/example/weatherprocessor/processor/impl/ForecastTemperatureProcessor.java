package com.example.weatherprocessor.processor.impl;

import com.example.weatherprocessor.model.CombinedDailyForecast;
import com.example.weatherprocessor.model.CombinedHourlyForecast;
import com.example.weatherprocessor.processor.DailyTemperatureExtremesProcessor;
import com.example.weatherprocessor.processor.HourlyDataProcessor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ForecastTemperatureProcessor implements HourlyDataProcessor, DailyTemperatureExtremesProcessor {

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
    public Map<LocalDate, Map<String, Integer>> calculateDailyTemperatureExtremes(Map<LocalDate, CombinedDailyForecast> combinedDailyForecasts) {
        Map<LocalDate, Map<String, Integer>> dailyTemperatureExtremes = new TreeMap<>();

        for (Map.Entry<LocalDate, CombinedDailyForecast> entry : combinedDailyForecasts.entrySet()) {
            LocalDate date = entry.getKey();
            CombinedDailyForecast dailyForecast = entry.getValue();

            List<Double> allTemperatures = dailyForecast.getHourlyForecasts().stream()
                    .flatMap(hourlyForecast -> hourlyForecast.getTemperatures().stream())
                    .map(temp -> Math.round(temp * 10) / 10.0)
                    .toList();
            //System.out.println("All temperatures for " + date + ": " + allTemperatures);

            List<Double> lowestTemperatures = allTemperatures.stream()
                    .sorted()
                    .limit(10)
                    .toList();

            List<Double> highestTemperatures = allTemperatures.stream()
                    .sorted(Comparator.reverseOrder())
                    .limit(10)
                    .toList();
           // System.out.println("Lowest 10 temperatures for " + date + ": " + lowestTemperatures);
            //System.out.println("Highest 10 temperatures for " + date + ": " + highestTemperatures);

            double averageMinTemperature  = lowestTemperatures.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);

            double averageMaxTemperature  = highestTemperatures.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);

            int roundedMaxTemperature = (int) Math.round(averageMaxTemperature);
            int roundedMinTemperature = (int) Math.round(averageMinTemperature);

            Map<String, Integer> extremes = new HashMap<>();
            extremes.put("max", roundedMaxTemperature);
            extremes.put("min", roundedMinTemperature);

            dailyTemperatureExtremes.put(date, extremes);
        }

        return dailyTemperatureExtremes;
    }
}
