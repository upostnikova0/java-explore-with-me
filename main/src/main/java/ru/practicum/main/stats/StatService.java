package ru.practicum.main.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StatService {
    private static final String APP_NAME = "ewm-main-service";
    private static final String URI_BASE = "/events/";
    private static final LocalDateTime START_TIME = LocalDateTime.of(2000, 1, 1, 0, 0);
    private final StatsClient client;

    public Long getViews(Long eventId) {
        String[] uris = new String[1];
        uris[0] = URI_BASE + eventId;
        List<ViewStats> statViews = client.getStats(START_TIME, LocalDateTime.now(), uris, true);
        if (statViews.isEmpty())
            return 0L;
        return statViews.get(0).getHits();
    }

    public Map<Long, Long> getViews(List<Long> ids) {
        String[] uris = new String[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            uris[i] = URI_BASE + ids.get(i).toString();
        }
        List<ViewStats> statViews = client.getStats(START_TIME, LocalDateTime.now(), uris, true);
        return statViews.stream().collect(Collectors.toMap(x -> Long.parseLong(x.getUri().replace(URI_BASE, "")), ViewStats::getHits));
    }

    public void saveViews(Long eventId, String ip) {
        String uri;
        if (!eventId.equals(0L)) {
            uri = URI_BASE + eventId;
        } else {
            uri = "/events";
        }
        EndpointHit endpointHit = new EndpointHit(null, APP_NAME, uri, ip, LocalDateTime.now());
        client.saveHit(endpointHit);
    }
}
