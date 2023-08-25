package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.server.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {
    String NEW_VIEW_STATS = "new ru.practicum.stats.dto.ViewStats";

    @Query("SELECT " + NEW_VIEW_STATS + "(h.app, h.uri, COUNT(DISTINCT h.ip)) " + "FROM Hit AS h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStats> findHitsWithoutUriAndUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT " + NEW_VIEW_STATS + "(h.app, h.uri, COUNT(DISTINCT h.ip)) " + "FROM Hit AS h " +
            "WHERE h.uri IN (?1) AND h.timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStats> findHitsWithUriAndUniqueIp(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("SELECT " + NEW_VIEW_STATS + "(h.app, h.uri, COUNT(h.uri)) " + "FROM Hit AS h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT (h.uri) DESC")
    List<ViewStats> findHitsWithoutUri(LocalDateTime start, LocalDateTime end);

    @Query("SELECT " + NEW_VIEW_STATS + "(h.app, h.uri, COUNT(h.uri)) " + "FROM Hit AS h " +
            "WHERE h.uri IN (?1) AND h.timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT (h.uri) DESC")
    List<ViewStats> findHitsWithUri(List<String> uris, LocalDateTime start, LocalDateTime end);
}
