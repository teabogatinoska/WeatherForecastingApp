package com.example.WeatherForecastingApp.weatherfetcher.consumer;

import com.example.WeatherForecastingApp.apigateway.dto.UserWeatherRequestDto;
import com.example.WeatherForecastingApp.common.RedisCacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Service
public class WeatherFetcherService {

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherFetcherService(RedisCacheService redisCacheService) {
        this.redisCacheService = redisCacheService;
    }

    private static final String GEO_API_URL = "https://geocoding-api.open-meteo.com/v1/search?name=";
    private static final String GEO_API_PARAMS = "&count=1&language=en&format=json";


    private static final String[] WEATHER_API_URLS = {
            "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&hourly=temperature_2m"
    };

    private static final String[] TOPICS = {
            "weather-api1-data",
            "weather-api2-data",
            "weather-api3-data",
            "weather-api4-data",
            "weather-api5-data",

    };

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
            } else {
                System.out.println("Cache miss. Fetching data from external APIs.");
                double[] coordinates = fetchLocationCoordinates(location);

                if (coordinates != null) {
                    double latitude = coordinates[0];
                    double longitude = coordinates[1];

                    ArrayList<String> weatherDataList = new ArrayList<>();
                    for (int i = 0; i < WEATHER_API_URLS.length; i++) {
                        String weatherData = fetchWeatherDataFromAPI(String.format(WEATHER_API_URLS[i], latitude, longitude));
                        System.out.println("Weather data: " + weatherData);
                        weatherDataList.add(weatherData);
                        kafkaTemplate.send(TOPICS[i], weatherData);
                    }
                } else {
                    System.out.println("Failed to fetch coordinates for location: " + location);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception, e.g., log it or send an error message to a Kafka topic
        }
    }

    private String fetchWeatherDataFromAPI(String apiUrl) {
        return restTemplate.getForObject(apiUrl, String.class);
    }

    private double[] fetchLocationCoordinates(String location){
        String url = GEO_API_URL + location + GEO_API_PARAMS;
        try{
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode results = root.path("results");
            if(results.isArray() && results.size() > 0){
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
}
