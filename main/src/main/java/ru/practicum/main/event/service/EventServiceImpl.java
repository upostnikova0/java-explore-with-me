package ru.practicum.main.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.mapper.CommentMapper;
import ru.practicum.main.comment.repository.CommentRepository;
import ru.practicum.main.event.category.model.Category;
import ru.practicum.main.event.category.service.CategoryService;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.enums.AdminStatus;
import ru.practicum.main.event.enums.PublicStatus;
import ru.practicum.main.event.location.dto.LocationDto;
import ru.practicum.main.event.location.mapper.LocationMapper;
import ru.practicum.main.event.location.model.Location;
import ru.practicum.main.event.location.repository.LocationRepository;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.stats.StatService;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.service.UserService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final EventMapper eventMapper;
    private final StatService statService;
    private final LocationMapper locationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByOwner(Long userId, Integer from, Integer size) {
        userService.findById(userId);

        List<EventShortDto> foundEvents = eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size))
                .stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());
        log.info("found events: {}", foundEvents);
        return setViews(foundEvents);
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User user = userService.findById(userId);
        Category category = categoryService.findById(newEventDto.getCategory());

        LocalDateTime eventDate = newEventDto.getEventDate();
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "event date must be later than current time + 2 hours");
        }

        Location location = locationMapper.toEntity(newEventDto.getLocation());
        if (Boolean.FALSE.equals(locationRepository.existsByLatAndLon(location.getLat(), location.getLon()))) {
            location = locationRepository.save(location);
        }
        Event event = eventMapper.toEntity(newEventDto);
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setEventDate(eventDate);
        event.setLocation(location);
        event.setInitiator(user);
        event.setState(PublicStatus.PENDING);
        EventFullDto savedEvent = eventMapper.toFullDto(eventRepository.save(event));
        savedEvent.setComments(new ArrayList<>());
        savedEvent.setConfirmedRequests(0);
        savedEvent.setViews(0L);
        log.info("new event {} was added to db", savedEvent);
        return savedEvent;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByOwner(Long userId, Long eventId) {
        userService.findById(userId);
        Event event = findById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "only initiator can get event");
        }

        log.info("event with id {} was found", eventId);
        EventFullDto foundEvent = eventMapper.toFullDto(event);
        setComments(foundEvent);
        foundEvent.setViews(statService.getViews(eventId));
        return foundEvent;
    }

    @Override
    @Transactional
    public EventFullDto updateEventByOwner(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        userService.findById(userId);
        Event event = findById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "only initiator can update event");
        }
        if (event.getState().equals(PublicStatus.PUBLISHED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "published event cannot be update");
        }
        LocalDateTime eventDate = updateEvent.getEventDate();
        if (eventDate != null) {
            if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "event date must be later than current time + 2 hours");
            }
            event.setEventDate(eventDate);
        }
        Optional.ofNullable(updateEvent.getAnnotation()).ifPresent(event::setAnnotation);
        if (updateEvent.getCategory() != null) {
            Category category = categoryService.findById(updateEvent.getCategory());
            event.setCategory(category);
        }
        Optional.ofNullable(updateEvent.getDescription()).ifPresent(event::setDescription);
        LocationDto locationDto = updateEvent.getLocation();
        if (locationDto != null) {
            if (!locationRepository.existsByLatAndLon(locationDto.getLat(), locationDto.getLon())) {
                locationRepository.save(locationMapper.toEntity(locationDto));
            }
            event.setLocation(locationMapper.toEntity(locationDto));
        }
        Optional.ofNullable(updateEvent.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEvent.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEvent.getRequestModeration()).ifPresent(event::setRequestModeration);
        if (updateEvent.getStateAction() != null) {
            String updateState = updateEvent.getStateAction();
            if (updateState.equals("SEND_TO_REVIEW")) {
                event.setState(PublicStatus.PENDING);
            } else if (updateState.equals("CANCEL_REVIEW")) {
                event.setState(PublicStatus.CANCELED);
            }
        }
        Optional.ofNullable(updateEvent.getTitle()).ifPresent(event::setTitle);
        log.info("event {} was updated in db", event);
        EventFullDto updatedEvent = eventMapper.toFullDto(eventRepository.save(event));
        setComments(updatedEvent);
        updatedEvent.setViews(statService.getViews(updatedEvent.getId()));
        return updatedEvent;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsByAdmin(List<Long> users,
                                               List<PublicStatus> states,
                                               List<Long> categories,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Integer from,
                                               Integer size) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusYears(100);
        if (rangeStart != null) {
            start = rangeStart;
        }
        if (rangeEnd != null) {
            end = rangeEnd;
        }
        checkRanges(rangeStart, rangeEnd);
        List<Event> foundEvents = eventRepository.findAllFromAdmin(users, states, categories, start, end, PageRequest.of(from, size, Sort.by("id")));
        if (foundEvents.isEmpty()) {
            log.info("not found events with params");
            return new ArrayList<>();
        }
        log.info("found events: {}", foundEvents);
        List<EventFullDto> result = foundEvents.stream().map(eventMapper::toFullDto).collect(Collectors.toList());
        setComments(result);
        return setViewsFull(result);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {
        Event event = findById(eventId);
        if (updateEvent.getStateAction() != null) {
            AdminStatus updateStatus = AdminStatus.valueOf(updateEvent.getStateAction());
            if (event.getState().equals(PublicStatus.PUBLISHED) && updateStatus.equals(AdminStatus.REJECT_EVENT)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "published event cannot be reject");
            }
            if (!event.getState().equals(PublicStatus.PENDING) && updateStatus.equals(AdminStatus.PUBLISH_EVENT)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "event cannot be published");
            }
        }
        Optional.ofNullable(updateEvent.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEvent.getAnnotation()).ifPresent(event::setAnnotation);

        if (updateEvent.getCategory() != null) {
            Long categoryId = updateEvent.getCategory();
            Category category = categoryService.findById(categoryId);
            event.setCategory(category);
        }
        Optional.ofNullable(updateEvent.getDescription()).ifPresent(event::setDescription);
        if (updateEvent.getLocation() != null) {
            Location updateLocation = locationMapper.toEntity(updateEvent.getLocation());
            if (!locationRepository.existsByLatAndLon(updateLocation.getLat(), updateLocation.getLon())) {
                updateLocation = locationRepository.save(updateLocation);
            }
            event.setLocation(updateLocation);
        }
        Optional.ofNullable(updateEvent.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEvent.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEvent.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(updateEvent.getTitle()).ifPresent(event::setTitle);
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction().equals("PUBLISH_EVENT")) {
                event.setState(PublicStatus.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (updateEvent.getStateAction().equals("REJECT_EVENT")) {
                event.setState(PublicStatus.CANCELED);
            }
        }
        LocalDateTime minStart = LocalDateTime.now().plusHours(1);
        if (Duration.between(minStart, event.getEventDate()).isNegative()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "new event date should be after published date + 1 hour");
        }
        EventFullDto savedEvent = eventMapper.toFullDto(eventRepository.save(event));
        setComments(savedEvent);
        savedEvent.setViews(statService.getViews(savedEvent.getId()));
        log.info("updated event {} was successfully saved", savedEvent);
        return savedEvent;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long eventId, String ip) {
        Event event = findById(eventId);
        if (event.getState() != PublicStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "event was not published");
        }
        EventFullDto eventFullDto = eventMapper.toFullDto(event);
        setComments(eventFullDto);
        statService.saveViews(eventId, ip);
        eventFullDto.setViews(statService.getViews(eventId));
        log.info("found event: {}", eventFullDto);
        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(String text,
                                         List<Long> categoryIds,
                                         Boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Boolean onlyAvailable,
                                         String sort,
                                         Integer from,
                                         Integer size,
                                         String ip) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusYears(100);
        if (rangeStart != null) {
            start = rangeStart;
        }
        if (rangeEnd != null) {
            end = rangeEnd;
        }

        if (end.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start should be before end");
        }
        List<EventShortDto> foundEvents = eventRepository
                .findEvents(PublicStatus.PUBLISHED, text, categoryIds, paid, start, end, onlyAvailable, PageRequest.of(from, size, Sort.by("id")))
                .stream().map(eventMapper::toShortDto).collect(Collectors.toList());
        statService.saveViews(0L, ip);
        setViews(foundEvents);
        if (sort != null) {
            switch (sort) {
                case "EVENT_DATE":
                    foundEvents = foundEvents
                            .stream()
                            .sorted(Comparator.comparing(EventShortDto::getEventDate))
                            .collect(Collectors.toList());
                    break;
                case "VIEWS":
                    foundEvents = foundEvents
                            .stream()
                            .sorted(Comparator.comparing(EventShortDto::getViews))
                            .collect(Collectors.toList());
                    break;
                default:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot be sorted by %s" + sort);
            }
        }
        log.info("found events: {}", foundEvents);
        return foundEvents;
    }

    @Override
    @Transactional(readOnly = true)
    public Event findById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("event with id = %d was not found", eventId))
                );
    }

    @Override
    public void updateEvent(Event event) {
        eventRepository.save(event);
    }

    private void checkRanges(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start should be before end");
        }
    }

    private List<EventShortDto> setViews(List<EventShortDto> events) {
        Map<Long, Long> views = statService.getViews(events.stream().map(EventShortDto::getId).collect(Collectors.toList()));
        for (EventShortDto event : events) {
            Long viewsNum = views.get(event.getId());
            if (viewsNum == null)
                viewsNum = 0L;
            event.setViews(viewsNum);
        }
        return events;
    }

    private List<EventFullDto> setViewsFull(List<EventFullDto> events) {
        Map<Long, Long> views = statService.getViews(events.stream().map(EventFullDto::getId).collect(Collectors.toList()));
        for (EventFullDto event : events) {
            Long viewsNum = views.get(event.getId());
            if (viewsNum == null)
                viewsNum = 0L;
            event.setViews(viewsNum);
        }
        return events;
    }

    public List<CommentDto> getComments(Long eventId) {
        Event event = findById(eventId);
        List<CommentDto> foundComments = commentRepository
                .findAllByEventId(event.getId())
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
        log.info("found comments: {}", foundComments);
        return foundComments;
    }

    private void setComments(List<EventFullDto> events) {
        for (EventFullDto event : events) {
            List<CommentDto> comments = getComments(event.getId());
            event.setComments(comments);
        }
    }

    private void setComments(EventFullDto event) {
        List<CommentDto> comments = getComments(event.getId());
        event.setComments(comments);
    }
}
