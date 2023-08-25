package ru.practicum.stats.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.server.HitMapper;
import ru.practicum.stats.server.repository.HitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;
    private final HitMapper hitMapper;

    @Autowired
    public HitServiceImpl(HitRepository hitRepository, @Qualifier("hitMapper") HitMapper hitMapper) {
        this.hitRepository = hitRepository;
        this.hitMapper = hitMapper;
    }

    @Override
    public void saveHit(EndpointHit endpointHit) {
        hitRepository.save(hitMapper.toHit(endpointHit));
    }

    @Override
    public List<ViewStats> getStats(String startDate, String endDate, List<String> uris, Boolean unique) {
        LocalDateTime start = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect request. Start date cant be after end date");
        }
        if (unique) {
            if (uris != null) {
                return hitRepository.findHitsWithUriAndUniqueIp(uris, start, end);
            }
            return hitRepository.findHitsWithoutUriAndUniqueIp(start, end);

        } else {
            if (uris != null) {
                return hitRepository.findHitsWithUri(uris, start, end);
            }
            return hitRepository.findHitsWithoutUri(start, end);
        }
    }
}
