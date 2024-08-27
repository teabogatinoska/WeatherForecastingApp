package com.example.WeatherForecastingApp.weatherprocessor.processor;

import com.example.WeatherForecastingApp.weatherprocessor.model.CombinedHourlyForecast;

import java.time.LocalDateTime;
import java.util.Map;

public interface HourlyDataProcessor  {
    Map<LocalDateTime, Integer> calculateHourlyData(Map<LocalDateTime, CombinedHourlyForecast> combinedHourlyForecasts);

}
