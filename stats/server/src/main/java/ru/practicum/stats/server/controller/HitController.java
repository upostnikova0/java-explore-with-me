package ru.practicum.stats.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.server.service.HitService;

import java.util.List;

@Slf4j
@RestController
public class HitController {
    private final HitService hitService;

    public HitController(HitService hitService) {
        this.hitService = hitService;
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam String start,
                                    @RequestParam String end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("get statistic for uris {}", uris);
        return hitService.getStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    public void saveHit(@RequestBody EndpointHit endpointHit) {
        log.info("save hit for uri {}", endpointHit.getUri());
        hitService.saveHit(endpointHit);
    }
}