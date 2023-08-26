package ru.practicum.stats.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.server.HitMapper;
import ru.practicum.stats.server.model.Hit;
import ru.practicum.stats.server.repository.HitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;
    private final HitMapper hitMapper;

    public HitServiceImpl(HitRepository hitRepository, HitMapper hitMapper) {
        this.hitRepository = hitRepository;
        this.hitMapper = hitMapper;
    }

    @Transactional
    @Override
    public void saveHit(EndpointHit endpointHit) {
        Hit hit = hitMapper.toHit(endpointHit);
        log.info("Information successfully saved: {}", hit);
        hitRepository.save(hit);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ViewStats> getStats(String startDate, String endDate, List<String> uris, Boolean unique) {
        LocalDateTime start = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (start.isAfter(end)) {
            log.info("Incorrect request. Start date cant be after end date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect request. Start date cant be after end date");
        }

        List<ViewStats> stats;
        if (unique) {
            if (uris != null) {
                stats = hitRepository.findHitsWithUriAndUniqueIp(uris, start, end);
                log.info("found statistic for uri by unique IP: {}", stats);
                return stats;
            }

            stats = hitRepository.findHitsWithoutUriAndUniqueIp(start, end);
            log.info("found statistic by unique IP: {}", stats);
        } else {
            if (uris != null) {
                stats = hitRepository.findHitsWithUri(uris, start, end);
                log.info("found statistic for uri: {}", stats);
                return stats;
            }
        }

        stats = hitRepository.findHitsWithoutUri(start, end);
        log.info("found statistic: {}", stats);
        return stats;
    }
}
