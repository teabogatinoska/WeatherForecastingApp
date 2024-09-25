package com.example.WeatherForecastingApp.apigateway.web;

import com.example.WeatherForecastingApp.common.dto.LocationDto;
import com.example.WeatherForecastingApp.apigateway.dto.UserWeatherRequestDto;
import com.example.WeatherForecastingApp.apigateway.service.LocationSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
public class WeatherRequestController {

    @Autowired
    private KafkaTemplate<String, UserDataRequestDto> kafkaTemplate;

    @Autowired
    private LocationSearchService locationSearchService;

    private static final String REQUEST_TOPIC = "user-weather-request";
    private static final String GEOAPIFY_API_URL = "https://api.geoapify.com/v1/geocode/reverse?lat={lat}&lon={lon}&apiKey=97d031e042404dea854c2f83b8ff9264";


    @PostMapping("/request")
    public String requestWeatherData(@RequestBody UserWeatherRequestDto requestDto) {

        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = GEOAPIFY_API_URL
                .replace("{lat}", requestDto.getLatitude().toString())
                .replace("{lon}", requestDto.getLongitude().toString());
        ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        List<Map<String, Object>> features = (List<Map<String, Object>>) responseBody.get("features");

        Map<String, Object> firstFeature = features.get(0);
        Map<String, Object> properties = (Map<String, Object>) firstFeature.get("properties");
        String city = (String) properties.get("city");
        String country = (String) properties.get("country");

        System.out.println("City and country:" + city + " and country:" + country);

        LocationDto locationDto = new LocationDto(city, country, requestDto.getLatitude(), requestDto.getLongitude());
        locationSearchService.updateRecentSearch(requestDto.getUserId(), locationDto);

        UserDataRequestDto userDataRequestDto = new UserDataRequestDto(requestDto.getUserId(), requestDto.getUsername(), locationDto);
        kafkaTemplate.send(REQUEST_TOPIC, userDataRequestDto);
        System.out.println(requestDto.toString());



        return "Weather request for " + city + ", " + country + " by user " + requestDto.getUsername() + " has been received";
    }
}
