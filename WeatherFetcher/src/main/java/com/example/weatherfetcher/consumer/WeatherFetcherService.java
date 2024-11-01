package com.example.weatherfetcher.consumer;

import com.example.common.EventStoreUtils;
import com.example.common.RedisCacheService;
import com.example.common.dto.LocationDto;
import com.example.common.dto.UserDataRequestDto;
import com.example.weatherfetcher.command.WeatherApiCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class
WeatherFetcherService {

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final List<WeatherApiCommand> weatherCommands;

    @Autowired
    private final EventStoreUtils eventStoreUtils;

    @Autowired
    public WeatherFetcherService(List<WeatherApiCommand> weatherCommands, EventStoreUtils eventStoreUtils) {
        this.weatherCommands = weatherCommands;
        this.eventStoreUtils = eventStoreUtils;

    }

    @KafkaListener(topics = "user-weather-request", groupId = "weather-fetcher-group")
    public void handleWeatherRequest(String message) {
        try {
            //redisCacheService.clearAllCache();
            UserDataRequestDto userDataRequestDto = objectMapper.readValue(message, UserDataRequestDto.class);
            String username = userDataRequestDto.getUsername();
            LocationDto location = userDataRequestDto.getLocation();

            String eventData = objectMapper.writeValueAsString(userDataRequestDto);
            eventStoreUtils.writeEventToEventStore("user-weather-requests", "UserWeatherRequest", eventData);

            Map<String, Map<LocalDateTime, Integer>> cachedHourlyData = redisCacheService.getCachedHourlyData(location.getName());
            System.out.println("CACHED HPURLY: " + cachedHourlyData);
            Map<LocalDate, Map<String, Integer>> cachedDailyData = redisCacheService.getCachedDailyData(location.getName());
            Map<LocalDateTime, Map<String, Double>> cachedAirQualityData = redisCacheService.getAirQualityData(location.getName());
            Map<LocalDateTime, String> cachedDescriptionData = redisCacheService.getDescriptionData(location.getName());

            if (cachedHourlyData != null && cachedDailyData != null && cachedDescriptionData != null) {
                System.out.println("Cache hit: " + location);

                Map<String, Object> hourlyMessage = new HashMap<>();
                hourlyMessage.put("username", username);
                hourlyMessage.put("location", location.getName());
                hourlyMessage.put("country", location.getCountry());
                hourlyMessage.put("hourlyResults", cachedHourlyData);
                hourlyMessage.put("airQualityResults", cachedAirQualityData);
                hourlyMessage.put("weatherDescriptions", cachedDescriptionData);

                System.out.println("weatherDescriptions Cached: " + cachedDescriptionData);

                Map<String, Object> dailyMessage = new HashMap<>();
                dailyMessage.put("username", username);
                dailyMessage.put("location", location.getName());
                dailyMessage.put("country", location.getCountry());
                dailyMessage.put("dailyResults", cachedDailyData);

                sendKafkaMessage("hourly-weather-data", hourlyMessage);
                sendKafkaMessage("daily-weather-data", dailyMessage);
            } else {
                System.out.println("Cache miss for location: " + location + ". Fetching data from external APIs.");

                for (WeatherApiCommand command : weatherCommands) {
                    try {
                        command.fetchWeatherData(userDataRequestDto, kafkaTemplate);
                    } catch (Exception e) {
                        System.err.println("Error executing " + command.getClass().getSimpleName() + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendKafkaMessage(String topic, Map<String, Object> message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topic, messageJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
