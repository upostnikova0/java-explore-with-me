package ru.practicum.main.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.service.RequestService;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    private final RequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("POST request by userId {} for eventId {}", userId, eventId);
        return service.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequestByRequester(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("PATCH cancel request by requesterId {} for requestId {}", userId, requestId);
        return service.cancelRequestByRequester(userId, requestId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getAllRequestsByRequester(@PathVariable Long userId) {
        log.info("GET all requests by requesterId {}", userId);
        return service.getAllRequestsByRequester(userId);
    }
}
