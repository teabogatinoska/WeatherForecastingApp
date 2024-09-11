package com.example.WeatherForecastingApp.authentication.controller;

import com.example.WeatherForecastingApp.common.dto.LocationDto;
import com.example.WeatherForecastingApp.authentication.model.Location;
import com.example.WeatherForecastingApp.authentication.model.User;
import com.example.WeatherForecastingApp.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/{userId}/favorite")
    public ResponseEntity<User> addFavoriteLocation(@PathVariable Long userId, @RequestBody LocationDto locationDto) {
        User user = userService.addFavoriteLocation(userId, locationDto.getName(), locationDto.getCountry(),
                locationDto.getLatitude(), locationDto.getLongitude());
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{userId}/favorite/{locationId}")
    public ResponseEntity<User> removeFavoriteLocation(@PathVariable Long userId, @PathVariable Long locationId) {
        User user = userService.removeFavoriteLocation(userId, locationId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{userId}/recent-search")
    public ResponseEntity<User> updateRecentSearch(@PathVariable Long userId, @RequestBody LocationDto locationDto) {
        User user = userService.addRecentSearch(userId, locationDto.getName(), locationDto.getCountry(),
                locationDto.getLatitude(), locationDto.getLongitude());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<Location>> getFavoriteLocations(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getFavoriteLocations(userId));
    }

    @GetMapping("/{userId}/recent-searches")
    public ResponseEntity<List<Location>> getRecentSearches(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getRecentSearches(userId));
    }

}
