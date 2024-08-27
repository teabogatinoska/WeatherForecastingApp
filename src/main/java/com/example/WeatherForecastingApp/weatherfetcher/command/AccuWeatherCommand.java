package com.example.WeatherForecastingApp.weatherfetcher.command;

import com.example.WeatherForecastingApp.apigateway.dto.UserWeatherRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class AccuWeatherCommand implements WeatherApiCommand {
    private static final String GEO_API_URL = "http://dataservice.accuweather.com/locations/v1/cities/search?apikey=z3rFJdAfM7bmfEaoCiMK3HhihzvK8tN8&q=";
    private static final String WEATHER_API_URL = "https://dataservice.accuweather.com/forecasts/v1/hourly/12hour/%s?apikey=z3rFJdAfM7bmfEaoCiMK3HhihzvK8tN8&details=true&metric=true";
    private static final String TOPIC = "weather-api5-data";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void fetchWeatherData(UserWeatherRequestDto requestDto, KafkaTemplate<String, String> kafkaTemplate) {
        String location = requestDto.getLocation();
        String locationId = fetchLocationId(location);
        try {
            if (locationId != null) {
                String apiUrl = String.format(WEATHER_API_URL, locationId);
                String weatherData = fetchWeatherDataFromAPI(apiUrl);

                Map<String, Object> message = new HashMap<>();
                message.put("username", requestDto.getUsername());
                message.put("location", requestDto.getLocation());
                message.put("weatherData", weatherData);

                String messageJson = objectMapper.writeValueAsString(message);
                kafkaTemplate.send(TOPIC, messageJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String fetchLocationId(String location) {
        String url = GEO_API_URL + location;
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            if (root.isArray() && root.size() > 0) {
                JsonNode firstResult = root.get(0);
                return firstResult.path("Key").asText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String fetchWeatherDataFromAPI(String apiUrl) {
        return restTemplate.getForObject(apiUrl, String.class);
    }
}
