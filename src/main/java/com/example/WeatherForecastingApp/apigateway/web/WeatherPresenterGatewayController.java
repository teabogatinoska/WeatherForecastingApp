package com.example.WeatherForecastingApp.apigateway.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
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
    public ResponseEntity<Map> getHourlyWeatherData(@RequestParam String username, @RequestParam String location, @RequestParam String country) {
        try {
            String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8.toString());
            String encodedCountry = URLEncoder.encode(country, StandardCharsets.UTF_8.toString());

            String url = weatherPresenterServiceUrl + "/forecast/hourly?username=" + username + "&location=" + encodedLocation
                    + "&country=" + encodedCountry;

            HttpHeaders headers = new HttpHeaders();

            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);


            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "An error occurred"));
        }
    }

    @GetMapping("/weather/daily")
    public ResponseEntity<Map> getDailyWeatherData(@RequestParam String username, @RequestParam String location, @RequestParam String country) {
        try {
            String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8.toString());
            String encodedCountry = URLEncoder.encode(country, StandardCharsets.UTF_8.toString());

            String url = weatherPresenterServiceUrl + "/forecast/daily?username=" + username + "&location=" + encodedLocation
                    + "&country=" + encodedCountry;

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "An error occurred"));
        }
    }

    @GetMapping("/weather/alerts")
    public ResponseEntity<Map> getWeatherAlerts(@RequestParam Long userId) {
        String url = weatherPresenterServiceUrl + "/forecast/alerts?userId=" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

}