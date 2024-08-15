package com.example.WeatherForecastingApp.weatherprocessor.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CombinedDailyForecast {
    private final List<CombinedHourlyForecast> hourlyForecasts = new ArrayList<>();

    public void addForecast(HourlyForecast forecast) {
        hourlyForecasts.add(new CombinedHourlyForecast(forecast.getTimestamp(), forecast.getTemperature()));
    }
    public List<CombinedHourlyForecast> getHourlyForecasts() {
        hourlyForecasts.sort(Comparator.comparing(CombinedHourlyForecast::getTimestamp));
        return hourlyForecasts;
    }
    @Override
    public String toString() {
        return "CombinedDailyForecast{" +
                "hourlyForecasts=" + hourlyForecasts.toString() +
                '}';
    }
}

