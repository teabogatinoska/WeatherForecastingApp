package com.example.WeatherForecastingApp.weatherpresenter.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherPresenterService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Map<LocalDateTime, Integer>> hourlyDataStore = new HashMap<>();
    private final Map<String, Map<LocalDate, Integer>> dailyDataStore = new HashMap<>();
    String currentUser;


    @KafkaListener(topics = "hourly-weather-data", groupId = "weather-presenter-group")
    public void receiveHourlyData(String messageJson) {
        try {
            Map<String, Object> message = objectMapper.readValue(messageJson, new TypeReference<Map<String, Object>>() {});

            String dataType = (String) message.get("dataType");
            this.currentUser = (String) message.get("username");
            Map<String, Integer> hourlyResults = (Map<String, Integer>) message.get("hourlyResults");

            Map<LocalDateTime, Integer> hourlyResultsConverted = new HashMap<>();
            for (Map.Entry<String, Integer> entry : hourlyResults.entrySet()) {
                LocalDateTime time = LocalDateTime.parse(entry.getKey());
                hourlyResultsConverted.put(time, entry.getValue());
            }

            hourlyDataStore.put(dataType, hourlyResultsConverted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "daily-weather-data", groupId = "weather-presenter-group")
    public void receiveDailyData(String messageJson) {
        try {
            Map<String, Object> message = objectMapper.readValue(messageJson, new TypeReference<Map<String, Object>>() {});

            String dataType = (String) message.get("dataType");
            this.currentUser = (String) message.get("username");
            Map<String, Integer> dailyResults = (Map<String, Integer>) message.get("dailyResults");

            Map<LocalDate, Integer> dailyResultsConverted = new HashMap<>();
            for (Map.Entry<String, Integer> entry : dailyResults.entrySet()) {
                LocalDate date = LocalDate.parse(entry.getKey());
                dailyResultsConverted.put(date, entry.getValue());
            }

            dailyDataStore.put(dataType, dailyResultsConverted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getHourlyData() {
        Map<String, Object> result = new HashMap<>();
        result.put("username", currentUser);
        result.put("hourlyData", hourlyDataStore);
        return result;
    }

    public Map<String, Object> getDailyData() {
        Map<String, Object> result = new HashMap<>();
        result.put("username", currentUser);
        result.put("dailyData", dailyDataStore);
        return result;
    }
}
