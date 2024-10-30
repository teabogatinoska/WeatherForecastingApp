package com.example.weatherprocessor.processor;

import com.example.weatherprocessor.model.CombinedDailyForecast;

import java.time.LocalDate;
import java.util.Map;

public interface DailyAverageDataProcessor {
    Map<LocalDate, Integer> calculateDailyData(Map<LocalDate, CombinedDailyForecast> combinedDailyForecasts);

}
