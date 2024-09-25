package com.example.WeatherForecastingApp.weatherpresenter.web;

import com.example.WeatherForecastingApp.weatherpresenter.consumer.WeatherPresenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/forecast")
public class WeatherPresenterController {

    @Autowired
    private final WeatherPresenterService weatherPresenterService;

    public WeatherPresenterController(WeatherPresenterService weatherPresenterService) {
        this.weatherPresenterService = weatherPresenterService;
    }


    @GetMapping(value="/hourly")
    public ResponseEntity<Map<String, Object>> getHourlyData(@RequestParam String username, @RequestParam String location, @RequestParam String country) {
        try {
            Map<String, Object> data = weatherPresenterService.getHourlyData(username, location, country);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping(value = "/daily")
    public ResponseEntity<Map<String, Object>> getDailyData(@RequestParam String username, @RequestParam String location, @RequestParam String country) {
        try {
            Map<String, Object> data = weatherPresenterService.getDailyData(username, location, country);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/alerts")
    public ResponseEntity<Map<String, Object>> getWeatherAlerts(@RequestParam Long userId) {
        try {
            Map<String, Object> data = weatherPresenterService.getWeatherAlerts(userId);
            if (data != null) {
                return ResponseEntity.ok(data);
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "No weather alerts available for this user."));

            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

}
