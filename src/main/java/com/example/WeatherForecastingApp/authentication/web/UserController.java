package com.example.WeatherForecastingApp.authentication.web;

import com.example.WeatherForecastingApp.authentication.exception.LocationAlreadyFavoriteException;
import com.example.WeatherForecastingApp.common.dto.LocationDto;
import com.example.WeatherForecastingApp.authentication.model.Location;
import com.example.WeatherForecastingApp.authentication.model.User;
import com.example.WeatherForecastingApp.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/{userId}/favorite-location")
    public ResponseEntity<?> addFavoriteLocation(@PathVariable Long userId, @RequestBody LocationDto locationDto) {
        try {
            User user = userService.addFavoriteLocation(userId, locationDto.getName(), locationDto.getCountry());
            return ResponseEntity.ok(user);
        } catch (LocationAlreadyFavoriteException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", ex.getMessage()));
        }
    }


    @DeleteMapping("/{userId}/favorite/{locationId}")
    public ResponseEntity<User> removeFavoriteLocation(@PathVariable Long userId, @PathVariable Long locationId) {
        User user = userService.removeFavoriteLocation(userId, locationId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{userId}/recent-search")
    public ResponseEntity<User> updateRecentSearch(@PathVariable Long userId, @RequestBody LocationDto locationDto) {
        User user = userService.addRecentSearch(userId, locationDto.getName(), locationDto.getCountry());
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

    @GetMapping("/users-favorites")
    public ResponseEntity<Map<Long, List<Location>>> getAllUsersFavoriteLocations() {
        Map<Long, List<Location>> allFavorites = userService.getAllUsersFavoriteLocations();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(allFavorites);
    }



}
