package com.example.WeatherForecastingApp.weatherfetcher.command;

import com.example.WeatherForecastingApp.apigateway.dto.UserWeatherRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherCommand implements WeatherApiCommand{
    private static final String WEATHER_API_URL = "http://api.weatherapi.com/v1/forecast.json?key=6c9e69e77b61491183781339240307&q=%s&days=7&aqi=no&alerts=no";
    private static final String TOPIC = "weather-api3-data";

    private final RestTemplate restTemplate = new RestTemplate();
    @Override
    public void fetchWeatherData(UserWeatherRequestDto requestDto, KafkaTemplate<String, String> kafkaTemplate) {
        String location = requestDto.getLocation();
        String apiUrl = String.format(WEATHER_API_URL, location);
        String weatherData = fetchWeatherDataFromAPI(apiUrl);
        kafkaTemplate.send(TOPIC, weatherData);
    }

    private String fetchWeatherDataFromAPI(String apiUrl) {
        return restTemplate.getForObject(apiUrl, String.class);
    }
}
