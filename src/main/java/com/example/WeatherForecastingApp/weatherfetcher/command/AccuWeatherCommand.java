package com.example.WeatherForecastingApp.weatherfetcher.command;

import com.example.WeatherForecastingApp.common.dto.UserDataRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
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
    public void fetchWeatherData(UserDataRequestDto requestDto, KafkaTemplate<String, String> kafkaTemplate) {
        String location = requestDto.getLocation().getName();
        Double requestLatitude = requestDto.getLocation().getLatitude();
        Double requestLongitude = requestDto.getLocation().getLongitude();

        String locationId = fetchLocationId(location, requestLatitude, requestLongitude);

        try {
            if (locationId != null) {
                String apiUrl = String.format(WEATHER_API_URL, locationId);
                String weatherData = fetchWeatherDataFromAPI(apiUrl);

                Map<String, Object> message = new HashMap<>();
                message.put("username", requestDto.getUsername());
                message.put("location", requestDto.getLocation().getName());
                message.put("country", requestDto.getLocation().getCountry());
                message.put("weatherData", weatherData);

                String messageJson = objectMapper.writeValueAsString(message);
                kafkaTemplate.send(TOPIC, messageJson);
            } else {
                System.out.println("Location ID could not be found for: " + location);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String fetchLocationId(String location, Double requestLatitude, Double requestLongitude) {
        String url = String.format(GEO_API_URL, location);
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            if (root.isArray() && root.size() > 0) {
                for (JsonNode locationNode : root) {
                    double responseLatitude = locationNode.path("GeoPosition").path("Latitude").asDouble();
                    double responseLongitude = locationNode.path("GeoPosition").path("Longitude").asDouble();

                    if (compareCoordinates(requestLatitude, responseLatitude) && compareCoordinates(requestLongitude, responseLongitude)) {
                        return locationNode.path("Key").asText();
                    }
                }
            }
        } catch (HttpServerErrorException.ServiceUnavailable e) {
            System.err.println("AccuWeather API limit exceeded while fetching location ID: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean compareCoordinates(Double requestCoordinate, Double responseCoordinate) {

        double roundedRequest = Math.round(requestCoordinate * 10.0) / 10.0;
        double roundedResponse = Math.round(responseCoordinate * 10.0) / 10.0;

        if (roundedRequest == roundedResponse) {
            return true;
        }
        double tolerance = 0.01;
        return Math.abs(roundedRequest - roundedResponse) <= tolerance;
    }


    private String fetchWeatherDataFromAPI(String apiUrl) {
        return restTemplate.getForObject(apiUrl, String.class);
    }
}
