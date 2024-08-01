package com.example.WeatherForecastingApp.weatherfetcher.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class WeatherFetcherService {
    @KafkaListener(topics = "user-weather-request", groupId = "weather-fetcher-group")
    public void handleWeatherRequest(String message) {
        System.out.println("Received weather request: " + message);
        // TODO: Add logic to handle the weather request, fetch data from external APIs, and process it.
    }
}
