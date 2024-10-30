package com.example.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventRequest {

    private String streamName;
    private String eventType;
    private String eventData;

    public EventRequest () {}

    public EventRequest(String streamName, String eventType, String eventData) {
        this.streamName = streamName;
        this.eventType = eventType;
        this.eventData = eventData;
    }

    @Override
    public String toString() {
        return "EventRequest{" +
                "streamName='" + streamName + '\'' +
                ", eventType='" + eventType + '\'' +
                ", eventData='" + eventData + '\'' +
                '}';
    }
}
