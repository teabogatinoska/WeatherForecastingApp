package com.example.WeatherForecastingApp.authentication;

;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AuthenticationApplication {
    public static void main(String[] args) {
        System.setProperty("spring.config.name", "authentication");
        SpringApplication.run(AuthenticationApplication.class, args);
    }
}
