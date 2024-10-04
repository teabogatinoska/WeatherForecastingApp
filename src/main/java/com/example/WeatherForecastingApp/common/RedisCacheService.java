package com.example.WeatherForecastingApp.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final long CACHE_TTL = 12;

    public void cacheHourlyData(String location, Map<String, Map<LocalDateTime, Integer>> hourlyData) {
        try {
            String cacheKey = location + "_hourly";
            String dataJson = objectMapper.writeValueAsString(hourlyData);
            redisTemplate.opsForValue().set(cacheKey, dataJson, CACHE_TTL, TimeUnit.HOURS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Map<LocalDateTime, Integer>> getCachedHourlyData(String location) {
        try {
            String cacheKey = location + "_hourly";
            String dataJson = redisTemplate.opsForValue().get(cacheKey);
            if (dataJson != null) {
                return objectMapper.readValue(dataJson, new TypeReference<Map<String, Map<LocalDateTime, Integer>>>() {
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public void cacheDailyData(String location, Map<LocalDate, Map<String, Integer>> dailyData) {
        try {
            String cacheKey = location + "_daily";
            String dataJson = objectMapper.writeValueAsString(dailyData);
            redisTemplate.opsForValue().set(cacheKey, dataJson, CACHE_TTL, TimeUnit.HOURS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<LocalDate, Map<String, Integer>> getCachedDailyData(String location) {
        try {
            String cacheKey = location + "_daily";
            String dataJson = redisTemplate.opsForValue().get(cacheKey);
            if (dataJson != null) {
                return objectMapper.readValue(dataJson, new TypeReference<Map<LocalDate, Map<String, Integer>>>() {
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void cacheWeatherAlerts(Long userId, Map<String, Object> alertsData) {
        try {
            String cacheKey = "user_" + userId + "_alerts";
            String dataJson = objectMapper.writeValueAsString(alertsData);
            redisTemplate.opsForValue().set(cacheKey, dataJson, CACHE_TTL, TimeUnit.HOURS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public Map<String, Object> getCachedWeatherAlerts(Long userId) {
        try {
            String cacheKey = "user_" + userId + "_alerts";
            String dataJson = redisTemplate.opsForValue().get(cacheKey);
            if (dataJson != null) {
                return objectMapper.readValue(dataJson, new TypeReference<Map<String, Object>>() {
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void cacheUserHourlyData(String key, Map<String, Map<String, Integer>> hourlyData) {
        try {
            String dataJson = objectMapper.writeValueAsString(hourlyData);
            System.out.println("Cached hourly data for user: " + key);
            redisTemplate.opsForValue().set(key, dataJson, CACHE_TTL, TimeUnit.HOURS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Map<String, Integer>> getCachedUserHourlyData(String key) {
        try {

            String dataJson = redisTemplate.opsForValue().get(key);
            if (dataJson != null) {
                System.out.println("Returning cached hourly data for user: " + key);
                return objectMapper.readValue(dataJson, new TypeReference<Map<String, Map<String, Integer>>>() {
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void cacheUserDailyData(String key, Map<LocalDate, Map<String, Integer>> dailyData) {
        try {
            String dataJson = objectMapper.writeValueAsString(dailyData);

            System.out.println("Cached daily data for user: " + key);
            redisTemplate.opsForValue().set(key, dataJson, CACHE_TTL, TimeUnit.HOURS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<LocalDate, Map<String, Integer>> getCachedUserDailyData(String key) {
        try {
            String dataJson = redisTemplate.opsForValue().get(key);
            if (dataJson != null) {
                System.out.println("Returning cached daily data for user: " + key);
                return objectMapper.readValue(dataJson, new TypeReference<Map<LocalDate, Map<String, Integer>>>() {
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void cacheAirQualityData(String location, Map<LocalDateTime, Map<String, Double>> airQualityResults) {
        try {
            String airQualityResultsJson = objectMapper.writeValueAsString(airQualityResults);
            System.out.println("Caching hourly air data for location: " + location);
            String cacheKey = location + "_airQuality";
            redisTemplate.opsForValue().set(cacheKey, airQualityResultsJson);
        } catch (Exception e) {
            System.out.println("Error caching air quality data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Map<LocalDateTime, Map<String, Double>> getAirQualityData(String location) {
        try {
            String cacheKey = location + "_airQuality";
            System.out.println("Cached hourly air data for location: " + location);
            String airQualityResultsJson = (String) redisTemplate.opsForValue().get(cacheKey);

            if (airQualityResultsJson != null) {
                return objectMapper.readValue(airQualityResultsJson, new TypeReference<Map<LocalDateTime, Map<String, Double>>>() {
                });
            } else {
                System.out.println("No air quality data found for location: " + location);
            }
        } catch (Exception e) {
            System.out.println("Error retrieving air quality data from cache: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public Map<LocalDateTime, Map<String, Double>> getCachedUserAirQualityData(String airQualityCacheKey) {
        try {
            String airQualityResultsJson = (String) redisTemplate.opsForValue().get(airQualityCacheKey);
            System.out.println("Cached hourly air data for: " + airQualityCacheKey);
            if (airQualityResultsJson != null) {

                return objectMapper.readValue(airQualityResultsJson, new TypeReference<Map<LocalDateTime, Map<String, Double>>>() {
                });
            }
        } catch (Exception e) {
            System.out.println("Error retrieving air quality data from cache: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    public void cacheUserAirQualityData(String airQualityCacheKey, Map<LocalDateTime, Map<String, Double>> airQualityResults) {
        try {
            String airQualityResultsJson = objectMapper.writeValueAsString(airQualityResults);
            System.out.println("Caching hourly air data for: " + airQualityCacheKey);
            redisTemplate.opsForValue().set(airQualityCacheKey, airQualityResultsJson);
        } catch (Exception e) {
            System.out.println("Error caching user air quality data: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void clearCache(String key) {
        redisTemplate.delete(key);
    }

    public void clearAllCache() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    public void cacheDescriptionData(String location, Map<LocalDateTime, String> descriptionResultsMap) {
        try {
            String DescriptionResultsJson = objectMapper.writeValueAsString(descriptionResultsMap);
            System.out.println("Caching hourly _description for location: " + location);
            String cacheKey = location + "_description";
            redisTemplate.opsForValue().set(cacheKey, DescriptionResultsJson);
        } catch (Exception e) {
            System.out.println("Error caching _description data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public Map<LocalDateTime, String> getDescriptionData(String location) {
        try {
            String cacheKey = location + "_description";
            System.out.println("Cached hourly description for location: " + location);
            String descriptionResultsJson = (String) redisTemplate.opsForValue().get(cacheKey);

            if (descriptionResultsJson != null) {
                return objectMapper.readValue(descriptionResultsJson, new TypeReference<Map<LocalDateTime, String>>() {
                });
            } else {
                System.out.println("No description data found for location: " + location);
            }
        } catch (Exception e) {
            System.out.println("Error retrieving description data from cache: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public void cacheUserWeatherDescriptions(String descriptionCacheKey, Map<LocalDateTime, String> weatherDescriptions) {
        try {
            String descriptionResultsJson = objectMapper.writeValueAsString(weatherDescriptions);
            System.out.println("Caching hourly desc for: " + descriptionCacheKey);
            redisTemplate.opsForValue().set(descriptionCacheKey, descriptionResultsJson);
        } catch (Exception e) {
            System.out.println("Error caching user air quality data: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public Map<LocalDateTime, String> getCachedUserWeatherDescriptions(String descriptionKey) {
        try {
            String descriptionResultsJson = (String) redisTemplate.opsForValue().get(descriptionKey);
            System.out.println("Cached hourly desc data for: " + descriptionKey);
            if (descriptionResultsJson != null) {

                return objectMapper.readValue(descriptionResultsJson, new TypeReference<Map<LocalDateTime, String>>() {
                });
            }
        } catch (Exception e) {
            System.out.println("Error retrieving desc data from cache: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


}

