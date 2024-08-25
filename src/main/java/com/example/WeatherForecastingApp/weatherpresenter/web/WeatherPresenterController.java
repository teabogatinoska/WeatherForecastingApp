package com.example.WeatherForecastingApp.weatherpresenter.web;

import com.example.WeatherForecastingApp.weatherpresenter.consumer.WeatherPresenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public Map<String, Object> getHourlyData() {
        return weatherPresenterService.getHourlyData();
    }

    @GetMapping("/daily")
    public Map<String, Object> getDailyData() {
        return weatherPresenterService.getDailyData();
    }
}
