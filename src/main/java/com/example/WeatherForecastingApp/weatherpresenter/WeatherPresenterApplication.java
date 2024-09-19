package com.example.WeatherForecastingApp.weatherpresenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = { "com.example.WeatherForecastingApp.weatherpresenter",
        "com.example.WeatherForecastingApp.common"},exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class WeatherPresenterApplication {
    public static void main(String[] args) {
        System.setProperty("spring.config.name", "weather-presenter");
        SpringApplication.run(WeatherPresenterApplication.class, args);
    }
    @Bean
    //@LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
