package com.example.WeatherForecastingApp.weatherpresenter.consumer;

import com.example.WeatherForecastingApp.common.EventStoreUtils;
import com.example.WeatherForecastingApp.common.RedisCacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
public class WeatherPresenterService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private final EventStoreUtils eventStoreUtils;

    @Autowired
    private RedisCacheService redisCacheService;

    public WeatherPresenterService(EventStoreUtils eventStoreUtils) {
        this.eventStoreUtils = eventStoreUtils;
    }

    @KafkaListener(topics = "hourly-weather-data", groupId = "weather-presenter-group")
    public void receiveHourlyData(String messageJson) {
        try {

            eventStoreUtils.writeEventToEventStore("hourly-data-processed", "HourlyDataProcessed", messageJson);
            Map<String, Object> message = objectMapper.readValue(messageJson, new TypeReference<>() {
            });

            String currentUser = (String) message.get("username");
            String location = (String) message.get("location");
            Map<String, Map<String, Integer>> hourlyResults = (Map<String, Map<String, Integer>>) message.get("hourlyResults");
            String cacheKey = currentUser + "_" + location + "_hourly";
            redisCacheService.cacheUserHourlyData(cacheKey, hourlyResults);

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

            Map<LocalDate, Map<String, Integer>> reformattedData = new TreeMap<>();
            for (Map.Entry<String, Map<String, Integer>> dateEntry : dailyResults.entrySet()) {
                LocalDate date = LocalDate.parse(dateEntry.getKey());
                Map<String, Integer> dataMap = dateEntry.getValue();
                reformattedData.put(date, new TreeMap<>(dataMap));
            }

            String cacheKey = currentUser + "_" + location + "_daily";
            redisCacheService.cacheUserDailyData(cacheKey, reformattedData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "user-weather-alerts", groupId = "weather-presenter-group")
    public void receiveWeatherAlerts(String messageJson) {
        try {
            eventStoreUtils.writeEventToEventStore("alert-data-received", "AlertDataReceived", messageJson);

            Map<String, Object> message = objectMapper.readValue(messageJson, new TypeReference<>() {
            });

            Object userIdObject = message.get("userId");
            Long userId;

            if (userIdObject instanceof Integer) {
                userId = ((Integer) userIdObject).longValue();
            } else if (userIdObject instanceof Long) {
                userId = (Long) userIdObject;
            } else {
                throw new IllegalArgumentException("Invalid userId type");
            }
            redisCacheService.cacheWeatherAlerts(userId, message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Map<String, Object> getHourlyData(String username, String location) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Map<String, Integer>> hourlyData = redisCacheService.getCachedUserHourlyData(username + "_" + location);

        if (hourlyData != null) {
            result.put("username", username);
            result.put("location", location);
            result.put("hourlyData", new TreeMap<>(hourlyData));
        } else {
            throw new IllegalArgumentException("No matching hourly data found for the provided username and location.");
        }

        return result;
    }

    public Map<String, Object> getDailyData(String username, String location) {
        Map<String, Object> result = new HashMap<>();
        String cacheKey = username + "_" + location + "_daily";
        Map<LocalDate, Map<String, Integer>> cachedDailyData = redisCacheService.getCachedUserDailyData(cacheKey);

        if (cachedDailyData != null) {
            result.put("username", username);
            result.put("location", location);
            result.put("dailyData", new TreeMap<>(cachedDailyData));
        } else {
            throw new IllegalArgumentException("No matching daily data found for the provided username and location.");
        }

        return result;
    }

    public Map<String, Object> getWeatherAlerts(Long userId) {

        Map<String, Object> cachedAlerts = redisCacheService.getCachedWeatherAlerts(userId);

        if (cachedAlerts != null) {
            System.out.println("Returning cached alerts for user: " + userId);
            return cachedAlerts;
        } else {
            System.out.println("No alerts found in cache for user: " + userId);
            throw new IllegalArgumentException("No weather alerts found for the provided user ID.");
        }
    }

}
