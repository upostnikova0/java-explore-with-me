package ru.practicum.stats.server.service;

import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;

import java.util.List;

public interface HitService {
    void saveHit(EndpointHit endpointHit);

    List<ViewStats> getStats(String start, String end, List<String> uris, Boolean uniq);
}
