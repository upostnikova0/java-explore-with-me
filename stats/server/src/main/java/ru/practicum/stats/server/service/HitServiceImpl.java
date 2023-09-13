package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
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
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;
    private final HitMapper hitMapper;

    @Transactional
    @Override
    public void saveHit(EndpointHit endpointHit) {
        Hit hit = hitMapper.toHit(endpointHit);
        log.info("Information successfully saved: {}", hit);
        hitRepository.save(hit);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        if (start.isAfter(end)) {
            log.info("Incorrect request. Start date cant be after end date");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect request. Start date cant be after end date");
        }

        List<ViewStats> stats;
        if (Boolean.TRUE.equals(unique)) {
            if (uris != null) {
                stats = hitRepository.findHitsWithUriAndUniqueIp(uris, start, end);
                log.info("found statistic for uris by unique IP: {}", stats);
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
