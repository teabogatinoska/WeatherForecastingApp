package com.example.WeatherForecastingApp.weatherfetcher.consumer;

import com.example.WeatherForecastingApp.apigateway.dto.UserWeatherRequestDto;
import com.example.WeatherForecastingApp.common.EventStoreUtils;
import com.example.WeatherForecastingApp.common.RedisCacheService;
import com.example.WeatherForecastingApp.common.dto.EventRequest;
import com.example.WeatherForecastingApp.weatherfetcher.command.WeatherApiCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
            UserWeatherRequestDto userWeatherRequestDto = objectMapper.readValue(message, UserWeatherRequestDto.class);
            String username = userWeatherRequestDto.getUsername();
            String location = userWeatherRequestDto.getLocation();

            String eventData = objectMapper.writeValueAsString(userWeatherRequestDto);
            eventStoreUtils.writeEventToEventStore("user-weather-requests", "UserWeatherRequest", eventData);

            Map<String, Map<LocalDateTime, Integer>> cachedHourlyData = redisCacheService.getCachedHourlyData(location);
            Map<LocalDate, Map<String, Integer>> cachedDailyData = redisCacheService.getCachedDailyData(location);
            Map<LocalDateTime, Map<String, Double>> cachedAirQualityData = redisCacheService.getAirQualityData(location);

            if (cachedHourlyData != null && cachedDailyData != null) {
                System.out.println("Cache hit: " + location);

                Map<String, Object> hourlyMessage = new HashMap<>();
                hourlyMessage.put("username", username);
                hourlyMessage.put("location", location);
                hourlyMessage.put("hourlyResults", cachedHourlyData);
                hourlyMessage.put("airQualityResults", cachedAirQualityData);

                System.out.println("airQualityResults Cached: " + cachedAirQualityData);

                Map<String, Object> dailyMessage = new HashMap<>();
                dailyMessage.put("username", username);
                dailyMessage.put("location", location);
                dailyMessage.put("dailyResults", cachedDailyData);

                sendKafkaMessage("hourly-weather-data", hourlyMessage);
                sendKafkaMessage("daily-weather-data", dailyMessage);
            } else {
                System.out.println("Cache miss for location: " + location + ". Fetching data from external APIs.");

                for (WeatherApiCommand command : weatherCommands) {
                    try {
                        command.fetchWeatherData(userWeatherRequestDto, kafkaTemplate);
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
