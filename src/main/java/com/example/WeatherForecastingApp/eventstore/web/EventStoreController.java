package com.example.WeatherForecastingApp.eventstore.web;

import com.example.WeatherForecastingApp.common.dto.EventRequest;
import com.example.WeatherForecastingApp.eventstore.model.Event;
import com.example.WeatherForecastingApp.eventstore.service.EventStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/events")
public class EventStoreController {


    @Autowired
    private EventStoreService eventStoreService;

    @PostMapping("/write/{streamName}")
    public CompletableFuture<ResponseEntity<String>> writeEvent(@PathVariable String streamName, @RequestBody EventRequest eventRequest) {
        System.out.println("Received event write request for stream: " + streamName);
        System.out.println("Event: " + eventRequest.toString());

        Event event = new Event(eventRequest.getEventType(),eventRequest.getEventData() );

        return eventStoreService.writeEvent(event, streamName)
                .thenApply(result -> ResponseEntity.ok("Event written to stream: " + streamName))
                .exceptionally(ex -> ResponseEntity.status(500).body("Error: " + ex.getMessage()));
    }


    @GetMapping("/read/{streamName}/{eventNumber}")
    public CompletableFuture<ResponseEntity<Event>> readEvent(
            @PathVariable String streamName,
            @PathVariable long eventNumber) {

        return eventStoreService.readEventFromStream(streamName, eventNumber)
                .thenApply(resolvedEvent -> {

                    Event event = new Event(
                           resolvedEvent.getOriginalEvent().getEventId().toString(),
                            resolvedEvent.getOriginalEvent().getEventType(),
                            new String(resolvedEvent.getOriginalEvent().getEventData(), StandardCharsets.UTF_8)
                    );
                    return ResponseEntity.ok(event);
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return ResponseEntity.status(500).body(null);
                });
    }

}
