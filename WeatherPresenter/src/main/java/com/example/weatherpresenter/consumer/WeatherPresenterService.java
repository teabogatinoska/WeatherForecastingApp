package com.example.weatherpresenter.consumer;

import com.example.common.EventStoreUtils;
import com.example.common.RedisCacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
            String country = (String) message.get("country");
            Map<String, Map<String, Integer>> hourlyResults = (Map<String, Map<String, Integer>>) message.get("hourlyResults");
            String cacheKey = normalizeCacheKey(currentUser, location, country,"hourly");
            redisCacheService.cacheUserHourlyData(cacheKey, hourlyResults);

            Map<LocalDateTime, String> weatherDescriptions = (Map<LocalDateTime, String>) message.get("weatherDescriptions");
            if (weatherDescriptions != null && !weatherDescriptions.isEmpty()) {
                String descriptionCacheKey = normalizeCacheKey(currentUser, location, country,"descriptions");
                redisCacheService.cacheUserWeatherDescriptions(descriptionCacheKey, weatherDescriptions);
            }

            Map<LocalDateTime, Map<String, Double>> airQualityResults = (Map<LocalDateTime, Map<String, Double>>) message.get("airQualityResults");

            String airQualityCacheKey = normalizeCacheKey(currentUser, location, country,"airQuality");
            redisCacheService.cacheUserAirQualityData(airQualityCacheKey, airQualityResults);


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
            String country = (String) message.get("country");

            Map<String, Map<String, Integer>> dailyResults = (Map<String, Map<String, Integer>>) message.get("dailyResults");

            Map<LocalDate, Map<String, Integer>> reformattedData = new TreeMap<>();
            for (Map.Entry<String, Map<String, Integer>> dateEntry : dailyResults.entrySet()) {
                LocalDate date = LocalDate.parse(dateEntry.getKey());
                Map<String, Integer> dataMap = dateEntry.getValue();
                reformattedData.put(date, new TreeMap<>(dataMap));
            }

            String cacheKey = normalizeCacheKey(currentUser, location, country,"daily");
            redisCacheService.cacheUserDailyData(cacheKey, reformattedData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "user-weather-alerts", groupId = "weather-presenter-group")
    public void receiveWeatherAlerts(String messageJson) {
        try {
            eventStoreUtils.writeEventToEventStore("alert-data-received", "AlertDataReceived", messageJson);

            Map<String, Object> message = objectMapper.readValue(messageJson, new TypeReference<Map<String, Object>>() {
            });

            Long userId = ((Number) message.get("userId")).longValue();
            List<?> locationAlerts = (List<?>) message.get("locationAlerts");

            System.out.println("Received alerts for user: " + userId);

            if(locationAlerts != null) {
                redisCacheService.cacheWeatherAlerts(userId, message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Map<String, Object> getHourlyData(String username, String location, String country) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Map<String, Integer>> hourlyData = redisCacheService.getCachedUserHourlyData(normalizeCacheKey(username, location, country,"hourly"));
        Map<LocalDateTime, Map<String, Double>> airQualityData = redisCacheService.getCachedUserAirQualityData(normalizeCacheKey(username, location, country,"airQuality"));
        Map<LocalDateTime, String> weatherDescriptions = redisCacheService.getCachedUserWeatherDescriptions(normalizeCacheKey(username, location, country,"descriptions"));

       if (hourlyData != null) {
            result.put("username", username);
            result.put("location", location);
            result.put("country", country);
            result.put("hourlyData", new TreeMap<>(hourlyData));

            if (airQualityData != null) {
                result.put("airQualityData", new TreeMap<>(airQualityData));
            }

            if (weatherDescriptions != null && !weatherDescriptions.isEmpty()) {
                result.put("weatherDescriptions", new TreeMap<>(weatherDescriptions));
            }
        } else {
            throw new IllegalArgumentException("No matching hourly data found for the provided username and location.");
        }

        return result;
    }

    public Map<String, Object> getDailyData(String username, String location, String country) {
        Map<String, Object> result = new HashMap<>();
        Map<LocalDate, Map<String, Integer>> cachedDailyData = redisCacheService.getCachedUserDailyData(normalizeCacheKey(username, location, country,"daily"));

        if (cachedDailyData != null) {
            result.put("username", username);
            result.put("location", location);
            result.put("country", country);
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

    public String normalizeCacheKey(String username, String location, String country, String type) {

        String encodedLocation = URLEncoder.encode(location.trim(), StandardCharsets.UTF_8).replace("+", "%20");
        String encodedCountry = URLEncoder.encode(country.trim(), StandardCharsets.UTF_8).replace("+", "%20");

        encodedLocation = encodedLocation.replaceAll("%0A", "").replaceAll("%0D", "");
        encodedCountry = encodedCountry.replaceAll("%0A", "").replaceAll("%0D", "");

        return String.format("%s_%s_%s_%s", username, encodedLocation, encodedCountry, type);
    }




}
