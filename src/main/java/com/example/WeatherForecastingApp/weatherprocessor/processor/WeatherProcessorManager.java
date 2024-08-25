package com.example.WeatherForecastingApp.weatherprocessor.processor;

import com.example.WeatherForecastingApp.weatherprocessor.model.CombinedDailyForecast;
import com.example.WeatherForecastingApp.weatherprocessor.model.CombinedHourlyForecast;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeatherProcessorManager {

    private final Map<String, WeatherDataProcessor> processors = new HashMap<>();

    public WeatherProcessorManager() {

        processors.put("temperature", new ForecastTemperatureProcessor());
        processors.put("humidity", new ForecastHumidityProcessor());
        processors.put("precipitation", new ForecastPrecipitationProcessor());
        processors.put("windSpeed", new ForecastWindProcessor());

    }

    public void processAllData(Map<LocalDateTime, CombinedHourlyForecast> combinedHourlyForecasts,
                               Map<LocalDate, CombinedDailyForecast> combinedDailyForecasts) {
        List<String> dataTypes = Arrays.asList("temperature", "humidity", "precipitation", "windSpeed");

        for (String type : dataTypes) {
            Map<LocalDateTime, Integer> hourlyResults = getHourlyData(type, combinedHourlyForecasts);
            Map<LocalDate, Integer> dailyResults = getDailyData(type, combinedDailyForecasts);

            System.out.println("Hourly " + type + " Averages:");
            printResults(hourlyResults);

            System.out.println("Daily " + type + " Averages:");
            printResults(dailyResults);
        }
    }


    public Map<LocalDateTime, Integer> getHourlyData(String type, Map<LocalDateTime, CombinedHourlyForecast> combinedHourlyForecasts) {
        WeatherDataProcessor processor = processors.get(type);
        if (processor != null) {
            return processor.calculateHourlyData(combinedHourlyForecasts);
        }
        return new HashMap<>();
    }

    public Map<LocalDate, Integer> getDailyData(String type, Map<LocalDate, CombinedDailyForecast> combinedDailyForecasts) {
        WeatherDataProcessor processor = processors.get(type);
        if (processor != null) {
            return processor.calculateDailyData(combinedDailyForecasts);
        }
        return new HashMap<>();
    }

    private void printResults(Map<?, Integer> results) {
        for (Map.Entry<?, Integer> entry : results.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
