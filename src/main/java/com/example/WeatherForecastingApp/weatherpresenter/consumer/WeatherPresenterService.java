package com.example.WeatherForecastingApp.weatherpresenter.consumer;

import com.example.WeatherForecastingApp.common.EventStoreUtils;
import com.example.WeatherForecastingApp.common.dto.LocationDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class WeatherPresenterService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Map<String, Map<LocalDateTime, Map<String, Integer>>>>  hourlyDataStore = new HashMap<>();
    private final Map<String, Map<String, Map<LocalDate, Map<String, Integer>>>> dailyDataStore = new TreeMap<>();
    private final Map<Long, Map<String, List<String>>> alertsDataStore = new HashMap<>();

    @Autowired
    private final EventStoreUtils eventStoreUtils;

    public WeatherPresenterService(EventStoreUtils eventStoreUtils) {
        this.eventStoreUtils = eventStoreUtils;
    }

    @KafkaListener(topics = "hourly-weather-data", groupId = "weather-presenter-group")
    public void receiveHourlyData(String messageJson) {
        try {

            eventStoreUtils.writeEventToEventStore("hourly-data-processed", "HourlyDataProcessed", messageJson);
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

            eventStoreUtils.writeEventToEventStore("daily-data-processed", "DailyDataProcessed", messageJson);

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

    @KafkaListener(topics = "user-weather-alerts", groupId = "weather-presenter-group")
    public void receiveWeatherAlerts(String messageJson) {
        try {
            eventStoreUtils.writeEventToEventStore("alert-data-received", "AlertDataReceived", messageJson);

            Map<String, Object> message = objectMapper.readValue(messageJson, new TypeReference<>() {});

            Object userIdObject = message.get("userId");
            Long userId;

            if (userIdObject instanceof Integer) {
                userId = ((Integer) userIdObject).longValue();
            } else if (userIdObject instanceof Long) {
                userId = (Long) userIdObject;
            } else {
                throw new IllegalArgumentException("Invalid userId type");
            }

            Map<String, Object> locationMap = (Map<String, Object>) message.get("location");
            LocationDto locationDto = objectMapper.convertValue(locationMap, LocationDto.class);

            List<String> alerts = (List<String>) message.get("alerts");

            alertsDataStore.putIfAbsent(userId, new HashMap<>());

            alertsDataStore.get(userId).put(locationDto.getName(), alerts);
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

    public Map<String, Object> getWeatherAlerts(Long userId) {
        Map<String, Object> result = new HashMap<>();
        System.out.println("Current alertsDataStore: " + alertsDataStore);


        if (alertsDataStore.containsKey(userId)) {
            result.put("user", userId);

            Map<String, List<String>> userAlerts = alertsDataStore.get(userId);
            result.put("alerts", userAlerts);

            System.out.println("Found alerts for user: " + userId + " - Alerts: " + userAlerts);

        } else {
            System.out.println("No alerts found for user: " + userId);
            throw new IllegalArgumentException("No weather alerts found for the provided username.");
        }

        return result;
    }


}
