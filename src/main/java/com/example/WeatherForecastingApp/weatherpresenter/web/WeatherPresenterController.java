package com.example.WeatherForecastingApp.weatherpresenter.web;

import com.example.WeatherForecastingApp.weatherpresenter.consumer.WeatherPresenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/forecast")
public class WeatherPresenterController {

    @Autowired
    private final WeatherPresenterService weatherPresenterService;

    public WeatherPresenterController(WeatherPresenterService weatherPresenterService) {
        this.weatherPresenterService = weatherPresenterService;
    }


    @GetMapping("/hourly")
    public ResponseEntity<Map<String, Object>> getHourlyData(@RequestParam String username, @RequestParam String location) {
        try {
            Map<String, Object> data = weatherPresenterService.getHourlyData(username, location);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/daily")
    public ResponseEntity<Map<String, Object>> getDailyData(@RequestParam String username, @RequestParam String location) {
        try {
            Map<String, Object> data = weatherPresenterService.getDailyData(username, location);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/alerts")
    public ResponseEntity<Map<String, Object>> getWeatherAlerts(@RequestParam Long userId) {
        try {
            Map<String, Object> data = weatherPresenterService.getWeatherAlerts(userId);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

}
