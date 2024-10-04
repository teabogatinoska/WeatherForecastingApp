package com.example.WeatherForecastingApp.weatheralert.model;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WeatherAlertEvent {
    private Long userId;
    private List<LocationAlerts> locationAlerts;
}
