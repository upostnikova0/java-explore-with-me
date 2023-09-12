package ru.practicum.main.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.main.event.enums.PublicStatus;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.service.EventService;
import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.enums.RequestStatus;
import ru.practicum.main.request.mapper.RequestMapper;
import ru.practicum.main.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.model.EventRequestStatusUpdateResult;
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RequestServiceImpl implements RequestService {
    private final UserService userService;
    private final EventService eventService;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User requester = userService.findById(userId);
        Event event = eventService.findById(eventId);
        User owner = event.getInitiator();

        if (requester.getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "owner cannot send request");
        }
        if (!event.getState().equals(PublicStatus.PUBLISHED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "event was not published");
        }
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "request is already exists");
        }
        if (!event.getParticipantLimit().equals(0) && event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "participant limit has already been reached");
        }
        ParticipationRequest request = new ParticipationRequest();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setId(null);
        request.setRequester(requester);
        request.setStatus(RequestStatus.PENDING);
        if (!event.getRequestModeration() || event.getParticipantLimit().equals(0)) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        ParticipationRequestDto savedRequestDto = requestMapper.toDto(requestRepository.save(request));
        log.info("new request {} saved successfully", savedRequestDto);
        return savedRequestDto;
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequestByRequester(Long requesterId, Long requestId) {
        userService.findById(requesterId);
        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, requesterId);
        request.setStatus(RequestStatus.CANCELED);
        log.info("set RequestStatus.CANCELED to request: {}", request);
        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllRequestsByRequester(Long requesterId) {
        userService.findById(requesterId);
        List<ParticipationRequestDto> allRequestsByRequester = requestRepository.findAllByRequesterId(requesterId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
        log.info("found requests: {}", allRequestsByRequester);
        return allRequestsByRequester;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAllRequestsByOwnerEvent(Long userId, Long eventId) {
        userService.findById(userId);
        Event event = eventService.findById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only initiator can get requests");
        }
        List<ParticipationRequest> allRequests = requestRepository.findAllByEventId(eventId);
        if (allRequests.isEmpty()) {
            log.info("not found requests for event with id {} ", eventId);
            return new ArrayList<>();
        }
        log.info("found requests: {}", allRequests);
        return allRequests
                .stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestsStatusByEventOwner(Long userId, Long eventId, EventRequestStatusUpdateRequest request) {
        userService.findById(userId);
        Event event = eventService.findById(eventId);

        if (Objects.equals(event.getParticipantLimit(), event.getConfirmedRequests())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "participant limit has already been reached");
        }

        List<ParticipationRequestDto> confirmedRequestList = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequestList = new ArrayList<>();

        List<ParticipationRequest> requests = requestRepository.findAllByEventIdAndIdIn(eventId, request.getRequestIds()).stream()
                .peek(req -> {
                    if (req.getStatus() != RequestStatus.PENDING) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Status changing is prohibited");
                    }
                    if (request.getStatus() == RequestStatus.REJECTED) {
                        req.setStatus(request.getStatus());
                        rejectedRequestList.add(requestMapper.toDto(req));
                    }
                    if (event.getConfirmedRequests() < event.getParticipantLimit() &&
                            request.getStatus() == RequestStatus.CONFIRMED) {
                        req.setStatus(request.getStatus());
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                        confirmedRequestList.add(requestMapper.toDto(req));
                    } else {
                        req.setStatus(RequestStatus.REJECTED);
                        rejectedRequestList.add(requestMapper.toDto(req));
                    }
                })
                .collect(Collectors.toList());
        eventService.updateEvent(event);
        requestRepository.saveAll(requests);
        return new EventRequestStatusUpdateResult(confirmedRequestList, rejectedRequestList);
    }
}
