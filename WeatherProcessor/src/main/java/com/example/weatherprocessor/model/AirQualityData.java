package com.example.weatherprocessor.model;

public class AirQualityData{
        private final double pm10;
        private final double pm25;

        public AirQualityData(double pm10, double pm25) {
            this.pm10 = pm10;
            this.pm25 = pm25;
        }

        public double getPm10() {
            return pm10;
        }

        public double getPm25() {
            return pm25;
        }

        @Override
        public String toString() {
            return "AirQualityData{" +
                    "pm10=" + pm10 +
                    ", pm25=" + pm25 +
                    '}';
        }
}
