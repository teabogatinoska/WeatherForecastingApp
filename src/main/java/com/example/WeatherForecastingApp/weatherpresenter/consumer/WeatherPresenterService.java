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
    private final Map<String, Map<String, Map<LocalDateTime, Map<String, Integer>>>>  hourlyDataStore = new HashMap<>();
    private final Map<String, Map<String, Map<LocalDate, Map<String, Integer>>>> dailyDataStore = new TreeMap<>();


    @KafkaListener(topics = "hourly-weather-data", groupId = "weather-presenter-group")
    public void receiveHourlyData(String messageJson) {
        try {
            Map<String, Object> message = objectMapper.readValue(messageJson, new TypeReference<>() {});

            String currentUser = (String) message.get("username");
            String location = (String) message.get("location");
            Map<String, Map<String, Integer>> hourlyResults = (Map<String, Map<String, Integer>>) message.get("hourlyResults");

            hourlyDataStore.putIfAbsent(currentUser, new HashMap<>());
            hourlyDataStore.get(currentUser).putIfAbsent(location, new TreeMap<>());

            for (Map.Entry<String, Map<String, Integer>> entry : hourlyResults.entrySet()) {
                String dataType = entry.getKey();
                Map<LocalDateTime, Map<String, Integer>> hourlyResultsForLocation = hourlyDataStore.get(currentUser).get(location);

                for (Map.Entry<String, Integer> timeEntry : entry.getValue().entrySet()) {
                    LocalDateTime time = LocalDateTime.parse(timeEntry.getKey());
                    hourlyResultsForLocation.putIfAbsent(time, new HashMap<>());

                    hourlyResultsForLocation.get(time).put(dataType, timeEntry.getValue());
                }
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
            String currentUser = (String) message.get("username");
            String location = (String) message.get("location");

            Map<String, Map<String, Integer>> dailyResults = (Map<String, Map<String, Integer>>) message.get("dailyResults");
            dailyDataStore.putIfAbsent(currentUser, new HashMap<>());
            dailyDataStore.get(currentUser).putIfAbsent(location, new TreeMap<>());

            for (Map.Entry<String, Map<String, Integer>> dateEntry : dailyResults.entrySet()) {
                LocalDate date = LocalDate.parse(dateEntry.getKey());
                Map<String, Integer> dataMap = dateEntry.getValue();

                dailyDataStore.get(currentUser).get(location).put(date, new TreeMap<>(dataMap));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getHourlyData(String username, String location) {
        Map<String, Object> result = new HashMap<>();

        if (hourlyDataStore.containsKey(username) && hourlyDataStore.get(username).containsKey(location)) {
            result.put("username", username);
            result.put("location", location);
            result.put("hourlyData", new TreeMap<>(hourlyDataStore.get(username).get(location)));
        } else {
            throw new IllegalArgumentException("No matching hourly data found for the provided username and location.");
        }

        return result;
    }

    public Map<String, Object> getDailyData(String username, String location) {
        Map<String, Object> result = new HashMap<>();

        if (dailyDataStore.containsKey(username) && dailyDataStore.get(username).containsKey(location)) {
            result.put("username", username);
            result.put("location", location);
            result.put("dailyData", new TreeMap<>(dailyDataStore.get(username).get(location)));
        } else {
            throw new IllegalArgumentException("No matching daily data found for the provided username and location.");
        }

        return result;

    }
}
