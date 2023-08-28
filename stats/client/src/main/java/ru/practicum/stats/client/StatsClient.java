package ru.practicum.stats.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class StatsClient extends BaseClient {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public StatsClient(RestTemplateBuilder builder, @Value("http://localhost:9090") String serverUrl) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new).build()
        );
    }

    public ResponseEntity<Object> saveHit(HttpServletRequest request) {
        EndpointHit hitDto = EndpointHit.builder()
                .app((String) request.getAttribute("app_name"))
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().toString()).build();
        return post("/hit", hitDto);
    }

    public List<ViewStats> getStats(String start, String end, List<String> uris, boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        ResponseEntity<Object> objectResponseEntity = get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        return objectMapper.convertValue(objectResponseEntity.getBody(), new TypeReference<>() {
        });
    }
}