package com.example.apigateway.web;


import com.example.common.dto.request.LoginRequest;
import com.example.common.dto.request.SignUpRequest;
import com.example.common.dto.response.JwtResponse;
import com.example.common.dto.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    @PostMapping(value = "/signin", produces = "application/json")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        String authUrl = authServiceUrl + "/auth/signin";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);

        try {
            ResponseEntity<JwtResponse> response = restTemplate.exchange(
                    authUrl,
                    HttpMethod.POST,
                    entity,
                    JwtResponse.class
            );

            return response;
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        }
    }

    @PostMapping(value = "/signup", produces = "application/json")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        String authUrl = authServiceUrl + "/auth/signup";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<SignUpRequest> entity = new HttpEntity<>(signUpRequest, headers);

        try {

            ResponseEntity<MessageResponse> response = restTemplate.exchange(
                    authUrl,
                    HttpMethod.POST,
                    entity,
                    MessageResponse.class
            );

            return response;
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        }
    }
}
