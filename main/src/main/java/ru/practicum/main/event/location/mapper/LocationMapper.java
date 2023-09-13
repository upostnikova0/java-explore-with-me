package ru.practicum.main.event.location.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.main.event.location.dto.LocationDto;
import ru.practicum.main.event.location.model.Location;

@Component
public class LocationMapper {
    public LocationDto toDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon()).build();
    }

    public Location toEntity(LocationDto location) {
        return Location.builder()
                .lat(location.getLat())
                .lon(location.getLon()).build();
    }
}
