package com.example.WeatherForecastingApp.weatherpresenter.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
public class WeatherPresenterService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Map<LocalDateTime, Integer>> hourlyDataStore = new HashMap<>();
    private final Map<LocalDate, Map<String, Integer>> dailyDataStore = new TreeMap<>();
    String currentUser;


    @KafkaListener(topics = "hourly-weather-data", groupId = "weather-presenter-group")
    public void receiveHourlyData(String messageJson) {
        try {
            Map<String, Object> message = objectMapper.readValue(messageJson, new TypeReference<>() {
            });

            this.currentUser = (String) message.get("username");
            Map<String, Map<String, Integer>> hourlyResults = (Map<String, Map<String, Integer>>) message.get("hourlyResults");

            for (Map.Entry<String, Map<String, Integer>> entry : hourlyResults.entrySet()) {
                String dataType = entry.getKey();
                Map<LocalDateTime, Integer> hourlyResultsConverted = new TreeMap<>();

                for (Map.Entry<String, Integer> timeEntry : entry.getValue().entrySet()) {
                    LocalDateTime time = LocalDateTime.parse(timeEntry.getKey());
                    hourlyResultsConverted.put(time, timeEntry.getValue());
                }

                hourlyDataStore.put(dataType, hourlyResultsConverted);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "daily-weather-data", groupId = "weather-presenter-group")
    public void receiveDailyData(String messageJson) {
        try {
            Map<String, Object> message = objectMapper.readValue(messageJson, new TypeReference<>() {
            });
            this.currentUser = (String) message.get("username");

            Map<String, Map<String, Integer>> dailyResults = (Map<String, Map<String, Integer>>) message.get("dailyResults");

            for (Map.Entry<String, Map<String, Integer>> dateEntry : dailyResults.entrySet()) {
                LocalDate date = LocalDate.parse(dateEntry.getKey());
                Map<String, Integer> dataMap = dateEntry.getValue();

                if (!dailyDataStore.containsKey(date)) {
                    dailyDataStore.put(date, new TreeMap<>());
                }

                dailyDataStore.get(date).putAll(dataMap);
            }
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
