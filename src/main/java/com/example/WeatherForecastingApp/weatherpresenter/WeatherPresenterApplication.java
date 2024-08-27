package com.example.WeatherForecastingApp.weatherpresenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class WeatherPresenterApplication {
    public static void main(String[] args) {
        System.setProperty("spring.config.name", "weather-presenter");
        SpringApplication.run(WeatherPresenterApplication.class, args);
    }

}
