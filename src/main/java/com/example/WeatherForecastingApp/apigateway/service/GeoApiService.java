package com.example.WeatherForecastingApp.apigateway.service;


import com.example.WeatherForecastingApp.common.dto.LocationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class GeoApiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String GEO_API_URL = "https://geocoding-api.open-meteo.com/v1/search?name=%s&language=en&format=json";


    @Autowired
    public GeoApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<LocationDto> searchLocations(String query) throws JsonProcessingException {
        String url = String.format(GEO_API_URL, query);
        String response = restTemplate.getForObject(url, String.class);

        JsonNode root = objectMapper.readTree(response).path("results");
        List<LocationDto> locations = new ArrayList<>();
        if (root.isArray()) {
            for (JsonNode node : root) {
                LocationDto locationDto = new LocationDto(
                        node.path("name").asText(),
                        node.path("latitude").asDouble(),
                        node.path("longitude").asDouble(),
                        node.path("country").asText()
                );
                locations.add(locationDto);
            }
        }
        return locations;
    }
}
