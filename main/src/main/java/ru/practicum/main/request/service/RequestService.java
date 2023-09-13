package ru.practicum.main.request.service;

import ru.practicum.main.request.dto.ParticipationRequestDto;
import ru.practicum.main.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.main.request.model.EventRequestStatusUpdateResult;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequestByRequester(Long requesterId, Long requestId);

    List<ParticipationRequestDto> getAllRequestsByRequester(Long requesterId);

    List<ParticipationRequestDto> getAllRequestsByOwnerEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsStatusByEventOwner(Long userId, Long eventId, EventRequestStatusUpdateRequest request);
}
