package com.example.WeatherForecastingApp.weatherfetcher;

import com.example.WeatherForecastingApp.common.RedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = { "com.example.WeatherForecastingApp.weatherfetcher",
        "com.example.WeatherForecastingApp.common"}, exclude = {DataSourceAutoConfiguration.class })
@EnableDiscoveryClient
public class WeatherFetcherApplication {
    public static void main(String[] args) {
        System.setProperty("spring.config.name", "weather-fetcher");
        SpringApplication.run(WeatherFetcherApplication.class, args);
    }
}
