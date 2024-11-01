package com.example.weatherprocessor.processor;

import com.example.weatherprocessor.model.CombinedDailyForecast;

import java.time.LocalDate;
import java.util.Map;

public interface DailyTemperatureExtremesProcessor {
    Map<LocalDate, Map<String, Integer>> calculateDailyTemperatureExtremes(Map<LocalDate, CombinedDailyForecast> combinedDailyForecasts);

}
