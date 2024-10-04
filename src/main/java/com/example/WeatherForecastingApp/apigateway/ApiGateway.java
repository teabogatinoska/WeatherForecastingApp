package com.example.WeatherForecastingApp.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = { "com.example.WeatherForecastingApp.apigateway",
        "com.example.WeatherForecastingApp.common"}, exclude = {DataSourceAutoConfiguration.class })
public class ApiGateway {
    public static void main(String[] args) {
        System.setProperty("spring.config.name", "api-gateway");
        SpringApplication.run(ApiGateway.class, args);
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }
}
