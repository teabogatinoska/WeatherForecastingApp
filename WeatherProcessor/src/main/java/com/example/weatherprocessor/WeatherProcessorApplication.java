package com.example.weatherprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = { "com.example.weatherprocessor",
        "com.example.common"},exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class WeatherProcessorApplication {
    public static void main(String[] args) {
        //System.setProperty("spring.config.name", "weather-processor");
        SpringApplication.run(WeatherProcessorApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
