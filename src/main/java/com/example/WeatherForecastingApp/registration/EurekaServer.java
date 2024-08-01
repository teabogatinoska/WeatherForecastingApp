package com.example.WeatherForecastingApp.registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableEurekaServer
public class EurekaServer {
    public static void main(String[] args) {
        System.setProperty("spring.config.name", "eureka-server");
        SpringApplication.run(EurekaServer.class, args);
    }

}
