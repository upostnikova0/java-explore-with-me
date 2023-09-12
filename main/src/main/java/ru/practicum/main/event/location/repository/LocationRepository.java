package ru.practicum.main.event.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.event.location.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Boolean existsByLatAndLon(Float lat, Float lon);
}
