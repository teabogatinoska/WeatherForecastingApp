package com.example.WeatherForecastingApp.weatherfetcher.command;

import com.example.WeatherForecastingApp.apigateway.dto.UserWeatherRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OpenMeteoCommand implements WeatherApiCommand{

    private static final String GEO_API_URL = "https://geocoding-api.open-meteo.com/v1/search?name=";
    private static final String GEO_API_PARAMS = "&count=1&language=en&format=json";
    private static final String WEATHER_API_URL = "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&hourly=temperature_2m";
    private static final String TOPIC = "weather-api1-data";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void fetchWeatherData(UserWeatherRequestDto requestDto, KafkaTemplate<String, String> kafkaTemplate) {
        String location = requestDto.getLocation();
        double[] coordinates = fetchCoordinates(location);

        if (coordinates != null) {
            String apiUrl = String.format(WEATHER_API_URL, coordinates[0], coordinates[1]);
            String weatherData = fetchWeatherDataFromAPI(apiUrl);
            kafkaTemplate.send(TOPIC, weatherData);
        }
    }

    private double[] fetchCoordinates(String location) {
        String url = GEO_API_URL + location + GEO_API_PARAMS;
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode results = root.path("results");
            if (results.isArray() && results.size() > 0) {
                JsonNode firstResult = results.get(0);
                double latitude = firstResult.path("latitude").asDouble();
                double longitude = firstResult.path("longitude").asDouble();
                return new double[]{latitude, longitude};
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
