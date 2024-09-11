package com.example.WeatherForecastingApp.apigateway.web;


import com.example.WeatherForecastingApp.common.dto.request.LoginRequest;
import com.example.WeatherForecastingApp.common.dto.request.SignUpRequest;
import com.example.WeatherForecastingApp.common.dto.response.JwtResponse;
import com.example.WeatherForecastingApp.common.dto.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        String authUrl = authServiceUrl + "/auth/signin";
        System.out.println("Auth url: " + authUrl);
        System.out.println("Login request: " + loginRequest);
        return restTemplate.postForEntity(authUrl, loginRequest, JwtResponse.class);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        String authUrl = authServiceUrl + "/auth/signup";
        return restTemplate.postForEntity(authUrl, signUpRequest, MessageResponse.class);
    }
}
