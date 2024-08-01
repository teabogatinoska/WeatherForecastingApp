package com.example.WeatherForecastingApp.weatherfetcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@EnableDiscoveryClient
public class WeatherFetcherApplication {
    public static void main(String[] args) {
        System.setProperty("spring.config.name", "weather-fetcher");
        SpringApplication.run(WeatherFetcherApplication.class, args);
    }
}
