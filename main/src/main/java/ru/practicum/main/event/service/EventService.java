package ru.practicum.main.event.service;

import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.enums.PublicStatus;
import ru.practicum.main.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventFullDto> getEventsByAdmin(List<Long> users,
                                        List<PublicStatus> states,
                                        List<Long> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Integer from,
                                        Integer size);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest eventAdminRequest);

    List<EventShortDto> getEventsByOwner(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByOwner(Long userId, Long eventId);

    EventFullDto updateEventByOwner(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventShortDto> getEvents(String text,
                                  List<Long> categories,
                                  Boolean paid,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  Boolean onlyAvailable,
                                  String sort,
                                  Integer from,
                                  Integer size,
                                  String ip);

    EventFullDto getEventById(Long eventId, String ip);

    Event findById(Long eventId);

    void updateEvent(Event event);
}
