package com.example.WeatherForecastingApp.weatherfetcher.command;

import com.example.WeatherForecastingApp.apigateway.dto.UserWeatherRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class VisualCrossingCommand implements WeatherApiCommand{
    private static final String WEATHER_API_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/%s/next7days?unitGroup=metric&include=hours&key=WY2C5X7BA532TDR3PN8GXNPTW&contentType=json";
    private static final String TOPIC = "weather-api4-data";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

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
