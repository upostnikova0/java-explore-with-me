package ru.practicum.main.event.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.main.event.category.dto.CategoryDto;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.location.dto.LocationDto;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.user.dto.UserShortDto;

@Component
public class EventMapper {
    public Event toEntity(NewEventDto newEventDto) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .title(newEventDto.getTitle()).build();
    }

    public EventShortDto toShortDto(Event event) {
        CategoryDto category = new CategoryDto();
        category.setId(event.getCategory().getId());
        category.setName(event.getCategory().getName());

        UserShortDto initiator = new UserShortDto();
        initiator.setId(event.getInitiator().getId());
        initiator.setName(event.getInitiator().getName());

        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(category)
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(initiator)
                .paid(event.getPaid())
                .title(event.getTitle()).build();
    }

    public EventFullDto toFullDto(Event event) {
        CategoryDto category = new CategoryDto();
        category.setId(event.getCategory().getId());
        category.setName(event.getCategory().getName());

        UserShortDto initiator = new UserShortDto();
        initiator.setId(event.getInitiator().getId());
        initiator.setName(event.getInitiator().getName());

        LocationDto location = new LocationDto();
        location.setLat(event.getLocation().getLat());
        location.setLon(event.getLocation().getLon());

        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(category)
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(initiator)
                .location(location)
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn() == null
                        ? null
                        : event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle()).build();
    }
}
