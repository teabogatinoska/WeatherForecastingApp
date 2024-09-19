package com.example.WeatherForecastingApp.weatheralert;


import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;

import org.springframework.scheduling.annotation.EnableScheduling;



@SpringBootApplication(scanBasePackages = { "com.example.WeatherForecastingApp.weatheralert",
        "com.example.WeatherForecastingApp.common"}, exclude = {DataSourceAutoConfiguration.class })
@EnableDiscoveryClient
@EnableScheduling
public class WeatherAlertApplication {
    public static void main(String[] args) {
        System.setProperty("spring.config.name", "weather-alert");
        SpringApplication.run(com.example.WeatherForecastingApp.weatheralert.WeatherAlertApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestTemplate externalRestTemplate() {
        return new RestTemplate();
    }

}