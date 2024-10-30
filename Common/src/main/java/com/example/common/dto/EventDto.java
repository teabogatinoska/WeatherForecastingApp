package com.example.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDto {
    private String eventId;
    private String eventType;
    private String eventData;

    public EventDto() {}

    public EventDto(String eventId, String eventType, String eventData) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventData = eventData;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", eventData='" + eventData + '\'' +
                '}';
    }
}
