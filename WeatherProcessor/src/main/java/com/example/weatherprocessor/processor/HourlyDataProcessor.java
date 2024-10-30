package com.example.weatherprocessor.processor;

import com.example.weatherprocessor.model.CombinedHourlyForecast;

import java.time.LocalDateTime;
import java.util.Map;

public interface HourlyDataProcessor  {
    Map<LocalDateTime, Integer> calculateHourlyData(Map<LocalDateTime, CombinedHourlyForecast> combinedHourlyForecasts);

}
