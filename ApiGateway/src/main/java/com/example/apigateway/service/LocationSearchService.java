package com.example.apigateway.service;

import com.example.common.dto.LocationDto;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class LocationSearchService {
    @Autowired
    private final RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    public LocationSearchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Long updateRecentSearch(Long userId, LocationDto locationDto) {
        System.out.println("Inside API Service: " + locationDto.toString());
        String url = authServiceUrl + "/user/" + userId + "/recent-search";
        return restTemplate.postForObject(url, locationDto, Long.class);
    }

    public List<LocationDto> getRecentSearches(Long userId) {
        String url = authServiceUrl + "/user/" + userId + "/recent-searches";
        System.out.println("Url GET: " + url);
        ResponseEntity<List<LocationDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<LocationDto>>() {}
        );

        return response.getBody();
    }

    public void addFavoriteLocation(Long userId, LocationDto locationDto) {
        String url = authServiceUrl + "/user/" + userId + "/favorite-location";
        restTemplate.postForObject(url, locationDto, Void.class);
    }

    public List<LocationDto> getFavoriteLocations(Long userId) {
        String url = authServiceUrl + "/user/" + userId + "/favorites";
        ResponseEntity<List<LocationDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<LocationDto>>() {}
        );
        return response.getBody();
    }

    public void removeFavoriteLocation(Long userId, Long locationId) {
        String url = authServiceUrl + "/user/" + userId + "/favorite/" + locationId;
        System.out.println("Url DELETE: " + url);
        restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

}
