package com.example.weatherprocessor.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CombinedDailyForecast {
    private final List<CombinedHourlyForecast> hourlyForecasts = new ArrayList<>();

    public void addForecast(HourlyForecast forecast) {
        CombinedHourlyForecast hourlyForecast = new CombinedHourlyForecast(forecast.getTimestamp());
        hourlyForecast.addTemperature(forecast.getTemperature());
        hourlyForecast.addHumidity(forecast.getHumidity());
        hourlyForecast.addPrecipitationProbability(forecast.getPrecipitationProbability());
        hourlyForecast.addWindSpeed(forecast.getWindSpeed());
        hourlyForecasts.add(hourlyForecast);
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

