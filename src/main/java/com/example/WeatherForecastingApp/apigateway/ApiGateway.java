package com.example.WeatherForecastingApp.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class ApiGateway {
    public static void main(String[] args) {
        System.setProperty("spring.config.name", "api-gateway");
        SpringApplication.run(ApiGateway.class, args);
    }

    @Bean
    //@LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
