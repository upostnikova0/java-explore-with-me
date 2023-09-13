package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.dto.UpdateEventUserRequest;
import ru.practicum.main.event.service.EventService;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.model.EventRequestStatusUpdateResult;
import ru.practicum.main.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Slf4j
public class PrivateEventController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping
    public List<EventShortDto> getEventShortByOwner(@PathVariable Long userId,
                                                    @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("GET events by owner for private");
        return eventService.getEventsByOwner(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        log.info("POST event for private");
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventFullByOwner(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("GET event by owner for private");
        return eventService.getEventByOwner(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByOwner(@PathVariable Long userId, @PathVariable Long eventId,
                                           @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info("PATCH request for private /users/{}/events/{} received. Provided DTO: {}",
                userId, eventId, updateEventUserRequest);
        return eventService.updateEventByOwner(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByOwnerEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("GET all requests for event owner for private");
        return requestService.getAllRequestsByOwnerEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsStatusByOwner(@PathVariable Long userId,
                                                                      @PathVariable Long eventId,
                                                                      @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("PATCH all requests status by event owner for private");
        return requestService.updateRequestsStatusByEventOwner(userId, eventId, request);
    }
}
