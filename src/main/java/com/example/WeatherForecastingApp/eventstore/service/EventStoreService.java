package com.example.WeatherForecastingApp.eventstore.service;

import com.eventstore.dbclient.*;
import com.example.WeatherForecastingApp.eventstore.model.Event;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class EventStoreService {

    @Autowired
    private EventStoreDBClient eventStoreDBClient;

    @Autowired
    private ObjectMapper objectMapper;

    public EventStoreService(EventStoreDBClient eventStoreDBClient, ObjectMapper objectMapper) {
        this.eventStoreDBClient = eventStoreDBClient;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<WriteResult> writeEvent(Event event, String streamName) {
        try {
            EventData eventData = EventData.builderAsJson(event.getType(), event)
                    .eventId(UUID.randomUUID())
                    .build();

            return eventStoreDBClient.appendToStream(streamName, eventData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write event", e);
        }
    }


    public CompletableFuture<ResolvedEvent> readEventFromStream(String streamName, long eventNumber) {
        try {

            ReadStreamOptions options = ReadStreamOptions.get()
                    .fromRevision(eventNumber)
                    .maxCount(1);

            return eventStoreDBClient.readStream(streamName, options)
                    .thenApply(readResult -> {
                        if (!readResult.getEvents().isEmpty()) {
                            return readResult.getEvents().get(0);
                        } else {
                            throw new RuntimeException("No event found at the specified event number.");
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException("Failed to read event from stream", e);
        }
    }



}
