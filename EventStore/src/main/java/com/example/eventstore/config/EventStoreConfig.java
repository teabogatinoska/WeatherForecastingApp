package com.example.eventstore.config;

import com.eventstore.dbclient.EventStoreDBClient;
import com.eventstore.dbclient.EventStoreDBClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;


@Configuration
public class EventStoreConfig {

    @Bean
    public EventStoreDBClient eventStoreDBClient() {
        try {
            EventStoreDBClientSettings settings = EventStoreDBClientSettings.builder()
                    .addHost(new InetSocketAddress("localhost", 2113))
                    .defaultCredentials("admin", "changeit")
                    .tls(false)
                    .tlsVerifyCert(false)
                    .buildConnectionSettings();

            return EventStoreDBClient.create(settings);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize EventStoreDB client", e);
        }
    }
}