package com.example.WeatherForecastingApp.weatherfetcher.consumer;

import com.example.WeatherForecastingApp.apigateway.dto.UserWeatherRequestDto;
import com.example.WeatherForecastingApp.common.RedisCacheService;
import com.example.WeatherForecastingApp.weatherfetcher.command.WeatherApiCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherFetcherService {

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final List<WeatherApiCommand> weatherCommands;


    @Autowired
    public WeatherFetcherService(List<WeatherApiCommand> weatherCommands) {
        this.weatherCommands = weatherCommands;
    }

    @KafkaListener(topics = "user-weather-request", groupId = "weather-fetcher-group")
    public void handleWeatherRequest(String message) {
        try {
            UserWeatherRequestDto userWeatherRequestDto = objectMapper.readValue(message, UserWeatherRequestDto.class);
            String username = userWeatherRequestDto.getUsername();
            String location = userWeatherRequestDto.getLocation();
            System.out.println("INSIDE WEATHER REQUEST");

            ArrayList<String> cachedData = redisCacheService.getCachedData(location);

            if (cachedData != null) {
                System.out.println("Cache hit: " + cachedData);
                // TODO: Send the processed data to presenter microservice
            }
                System.out.println("Cache miss. Fetching data from external APIs.");
            for (WeatherApiCommand command : weatherCommands) {
                command.fetchWeatherData(userWeatherRequestDto, kafkaTemplate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception, e.g., log it or send an error message to a Kafka topic
        }
    }


}
