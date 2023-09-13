package ru.practicum.stats.server;

import org.springframework.stereotype.Component;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.server.model.Hit;

@Component
public class HitMapper {
    public Hit toHit(EndpointHit endpointHit) {
        return new Hit(
                null,
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                endpointHit.getTimestamp()
        );
    }
}
