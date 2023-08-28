package ru.practicum.stats.server;

import org.springframework.stereotype.Component;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.server.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class HitMapper {
    public Hit toHit(EndpointHit endpointHit) {
        return Hit.builder()
                .id(endpointHit.getId())
                .app(endpointHit.getApp())
                .ip(endpointHit.getIp())
                .uri(endpointHit.getUri())
                .timestamp(LocalDateTime.parse(
                        endpointHit.getTimestamp(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                )).build();
    }

    public ViewStats toViewStats(Hit hit) {
        return ViewStats.builder()
                .app(hit.getApp())
                .uri(hit.getUri()).build();
    }
}
