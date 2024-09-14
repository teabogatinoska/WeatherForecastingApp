package com.example.WeatherForecastingApp.eventstore;

import com.example.WeatherForecastingApp.authentication.security.jwt.AuthEntryPointJwt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class EventConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Applying Security Configuration");
        http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/events/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(new AuthEntryPointJwt())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);


        return http.build();
    }

}
