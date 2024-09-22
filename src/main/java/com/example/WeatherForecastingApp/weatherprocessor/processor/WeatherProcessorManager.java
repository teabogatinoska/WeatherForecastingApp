package com.example.WeatherForecastingApp.weatherprocessor.processor;

import com.example.WeatherForecastingApp.common.RedisCacheService;
import com.example.WeatherForecastingApp.weatherprocessor.model.AirQualityData;
import com.example.WeatherForecastingApp.weatherprocessor.model.CombinedDailyForecast;
import com.example.WeatherForecastingApp.weatherprocessor.model.CombinedHourlyForecast;
import com.example.WeatherForecastingApp.weatherprocessor.processor.impl.ForecastHumidityProcessor;
import com.example.WeatherForecastingApp.weatherprocessor.processor.impl.ForecastPrecipitationProcessor;
import com.example.WeatherForecastingApp.weatherprocessor.processor.impl.ForecastTemperatureProcessor;
import com.example.WeatherForecastingApp.weatherprocessor.processor.impl.ForecastWindProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeatherProcessorManager {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private RedisCacheService redisCacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, HourlyDataProcessor> hourlyProcessors = new HashMap<>();
    private final Map<String, DailyAverageDataProcessor> dailyAverageProcessors = new HashMap<>();
    private final Map<String, DailyTemperatureExtremesProcessor> dailyTemperatureExtremesProcessors = new HashMap<>();

    public WeatherProcessorManager() {

        hourlyProcessors.put("temperature", new ForecastTemperatureProcessor());
        hourlyProcessors.put("humidity", new ForecastHumidityProcessor());
        hourlyProcessors.put("precipitation", new ForecastPrecipitationProcessor());
        hourlyProcessors.put("windSpeed", new ForecastWindProcessor());

        dailyAverageProcessors.put("humidity", new ForecastHumidityProcessor());
        dailyAverageProcessors.put("precipitation", new ForecastPrecipitationProcessor());
        dailyAverageProcessors.put("windSpeed", new ForecastWindProcessor());

        dailyTemperatureExtremesProcessors.put("temperature", new ForecastTemperatureProcessor());

    }

    public void processAllData(Map<LocalDateTime, CombinedHourlyForecast> combinedHourlyForecasts,
                               Map<LocalDate, CombinedDailyForecast> combinedDailyForecasts, Map<LocalDateTime, AirQualityData> airQualityDataMap, String username, String location) {
        List<String> dataTypes = Arrays.asList("temperature", "humidity", "precipitation", "windSpeed");
        Map<String, Object> hourlyMessage = new HashMap<>();
        Map<String, Object> dailyMessage = new HashMap<>();
        hourlyMessage.put("username", username);
        hourlyMessage.put("location", location);
        dailyMessage.put("username", username);
        dailyMessage.put("location", location);

        Map<String, Map<LocalDateTime, Integer>> hourlyResultsMap = new HashMap<>();
        Map<LocalDate, Map<String, Integer>> dailyResultsMap = new HashMap<>();
        Map<LocalDateTime, String> descriptionResultsMap = new HashMap<>();


        for (String type : dataTypes) {
            Map<LocalDateTime, Integer> hourlyResults = getHourlyData(type, combinedHourlyForecasts);
            hourlyResultsMap.put(type, hourlyResults);


            if ("temperature".equals(type)) {
                Map<LocalDate, Map<String, Integer>> dailyTemperatureExtremes = getDailyTemperatureExtremes(type, combinedDailyForecasts);
                dailyTemperatureExtremes.forEach((date, extremes) -> {
                    dailyResultsMap.computeIfAbsent(date, k -> new HashMap<>()).putAll(extremes);
                });
            } else {
                Map<LocalDate, Integer> dailyResults = getDailyData(type, combinedDailyForecasts);
                dailyResults.forEach((date, value) -> {
                    dailyResultsMap.computeIfAbsent(date, k -> new HashMap<>()).put(type, value);
                });
            }
        }

        combinedHourlyForecasts.forEach((timestamp, combinedHourlyForecast) -> {
            if (combinedHourlyForecast.getDescription() != null) {
                System.out.println("Final description for " + timestamp + ": " + combinedHourlyForecast.getDescription());
                descriptionResultsMap.put(timestamp, combinedHourlyForecast.getDescription());
            }
        });


        Map<LocalDateTime, Map<String, Double>> airQualityResults = new HashMap<>();
        airQualityDataMap.forEach((timestamp, airQualityData) -> {
            Map<String, Double> airQualityValues = new HashMap<>();
            airQualityValues.put("pm10", airQualityData.getPm10());
            airQualityValues.put("pm2_5", airQualityData.getPm25());
            airQualityResults.put(timestamp, airQualityValues);

        });

        hourlyMessage.put("hourlyResults", hourlyResultsMap);
        hourlyMessage.put("airQualityResults", airQualityResults);
        dailyMessage.put("dailyResults", dailyResultsMap);
        hourlyMessage.put("weatherDescriptions", descriptionResultsMap);


        redisCacheService.cacheHourlyData(location, hourlyResultsMap);
        redisCacheService.cacheDailyData(location, dailyResultsMap);
        redisCacheService.cacheAirQualityData(location, airQualityResults);
        redisCacheService.cacheDescriptionData(location, descriptionResultsMap);

        sendKafkaMessage("hourly-weather-data", hourlyMessage);
        sendKafkaMessage("daily-weather-data", dailyMessage);
    }

    public Map<LocalDateTime, Integer> getHourlyData(String type, Map<LocalDateTime, CombinedHourlyForecast> combinedHourlyForecasts) {
        HourlyDataProcessor processor = hourlyProcessors.get(type);
        if (processor != null) {
            return processor.calculateHourlyData(combinedHourlyForecasts);
        }
        return new HashMap<>();
    }

    public Map<LocalDate, Integer> getDailyData(String type, Map<LocalDate, CombinedDailyForecast> combinedDailyForecasts) {
        DailyAverageDataProcessor processor = dailyAverageProcessors.get(type);
        if (processor != null) {
            return processor.calculateDailyData(combinedDailyForecasts);
        }
        return new HashMap<>();
    }

    private Map<LocalDate, Map<String, Integer>> getDailyTemperatureExtremes(String type, Map<LocalDate, CombinedDailyForecast> combinedDailyForecasts) {
        DailyTemperatureExtremesProcessor processor = dailyTemperatureExtremesProcessors.get(type);
        if (processor != null) {
            return processor.calculateDailyTemperatureExtremes(combinedDailyForecasts);
        }
        return new HashMap<>();
    }

    private void sendKafkaMessage(String topic, Map<String, Object> message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topic, messageJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
