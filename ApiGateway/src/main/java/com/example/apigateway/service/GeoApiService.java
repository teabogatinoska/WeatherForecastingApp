package com.example.apigateway.service;


import com.example.apigateway.dto.GeoLocationDto;
import com.example.common.dto.LocationDto;
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

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    private static final String GEO_API_URL = "https://geocoding-api.open-meteo.com/v1/search?name=%s&language=en&format=json";


    @Autowired
    public GeoApiService( ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<GeoLocationDto> searchLocations(String query) throws JsonProcessingException {
        String url = String.format(GEO_API_URL, query);
        String response = restTemplate.getForObject(url, String.class);

        JsonNode root = objectMapper.readTree(response).path("results");
        List<GeoLocationDto> locations = new ArrayList<>();
        if (root.isArray()) {
            for (JsonNode node : root) {
                GeoLocationDto locationDto = new GeoLocationDto(
                        node.path("name").asText(),
                        node.path("country").asText(),
                        node.path("latitude").asDouble(),
                        node.path("longitude").asDouble(),
                        node.path("admin1").asText()
                );
                locations.add(locationDto);
            }
        }
        return locations;
    }
}
