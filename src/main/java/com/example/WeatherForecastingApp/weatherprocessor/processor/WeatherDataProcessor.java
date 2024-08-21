package com.example.WeatherForecastingApp.weatherprocessor.processor;

import com.example.WeatherForecastingApp.weatherprocessor.model.CombinedDailyForecast;
import com.example.WeatherForecastingApp.weatherprocessor.model.CombinedHourlyForecast;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public interface WeatherDataProcessor {
    Map<LocalDateTime, Integer> calculateHourlyData(Map<LocalDateTime, CombinedHourlyForecast> combinedHourlyForecasts);
    Map<LocalDate, Integer> calculateDailyData(Map<LocalDate, CombinedDailyForecast> combinedDailyForecasts);

}
