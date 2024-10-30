package com.example.eventstore.model;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Event {
    private String id;
    private String type;
    private String data;

    public Event(String type, String data) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.data = data;
    }
}