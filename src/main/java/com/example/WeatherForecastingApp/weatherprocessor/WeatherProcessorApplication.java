package com.example.WeatherForecastingApp.weatherprocessor;

import com.example.WeatherForecastingApp.weatherfetcher.WeatherFetcherApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class WeatherProcessorApplication {
    public static void main(String[] args) {
        System.setProperty("spring.config.name", "weather-processor");
        SpringApplication.run(WeatherProcessorApplication.class, args);
    }
}
