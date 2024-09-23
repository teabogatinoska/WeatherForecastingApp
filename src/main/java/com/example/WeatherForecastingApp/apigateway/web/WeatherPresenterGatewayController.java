package com.example.WeatherForecastingApp.apigateway.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/data")
public class WeatherPresenterGatewayController {

    private final RestTemplate restTemplate;

    @Value("${weather.presenter.service.url}")
    private String weatherPresenterServiceUrl;

    public WeatherPresenterGatewayController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/weather/hourly")
    public ResponseEntity<Map> getHourlyWeatherData(@RequestParam String username, @RequestParam String location) {
        String url = weatherPresenterServiceUrl + "/forecast/hourly?username=" + username + "&location=" + location;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @GetMapping("/weather/daily")
    public ResponseEntity<Map> getDailyWeatherData(@RequestParam String username, @RequestParam String location) {
        String url = weatherPresenterServiceUrl + "/forecast/daily?username=" + username + "&location=" + location;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @GetMapping("/weather/alerts")
    public ResponseEntity<Map> getWeatherAlerts(@RequestParam Long userId) {
        String url = weatherPresenterServiceUrl + "/forecast/alerts?userId=" + userId;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}