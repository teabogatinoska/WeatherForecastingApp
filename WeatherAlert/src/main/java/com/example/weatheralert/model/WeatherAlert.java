package com.example.weatheralert.model;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WeatherAlert {
    private String headline;
    private String severity;
    private String urgency;
    private String areas;
    private String event;
    private LocalDateTime effectiveDateTime;
    private String effective;
    private LocalDateTime expiresDateTime;
    private String expires;

    public WeatherAlert(String headline, String severity, String urgency, String areas, String event,
                        String effective, String expires) {
        this.headline = headline;
        this.severity = severity;
        this.urgency = urgency;
        this.areas = areas;
        this.event = event;
        this.effective = effective;
        this.expires = expires;
        this.effectiveDateTime = parseToLocalDateTime(effective);
        this.expiresDateTime = parseToLocalDateTime(expires);
    }

    private LocalDateTime parseToLocalDateTime(String dateTimeStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (Exception e) {
            return null;
        }
    }

}

