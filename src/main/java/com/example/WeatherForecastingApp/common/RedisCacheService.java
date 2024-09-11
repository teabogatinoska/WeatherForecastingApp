package com.example.WeatherForecastingApp.common;

import com.example.WeatherForecastingApp.authentication.model.Location;
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


    public void clearCache(String key) {
        redisTemplate.delete(key);
    }
    public void clearAllCache() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }
}

