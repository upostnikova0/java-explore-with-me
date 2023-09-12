package ru.practicum.main.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main.compilation.mapper.CompilationMapper;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.repository.CompilationRepository;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilation) {
        Compilation compilation = compilationMapper.toEntity(newCompilation);
        List<Event> events = new ArrayList<>();
        if (newCompilation.getEvents() != null) {
            events = eventRepository.findAllById(newCompilation.getEvents());
            if (events.size() != newCompilation.getEvents().size())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You are adding not existing event. Check event IDs");
        }
        compilation.setEvents(new LinkedHashSet<>(events));

        CompilationDto compilationDto = compilationMapper.toDto(compilationRepository.save(compilation));
        compilationDto.setEvents(events.stream().map(eventMapper::toShortDto).collect(Collectors.toList()));
        log.info("new compilation {} was saved successfully", compilationDto);
        return compilationDto;
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compilationId) {
        Compilation compilation = findById(compilationId);
        log.info("compilation with id {} was deleted", compilationId);
        compilationRepository.deleteById(compilation.getId());
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest updatedCompilation) {
        Compilation compilation = findById(compilationId);
        List<Event> events = new ArrayList<>();
        if (updatedCompilation.getEvents() != null) {
            events = eventRepository.findAllById(updatedCompilation.getEvents());
            if (events.size() != updatedCompilation.getEvents().size()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You are adding not existing event. Check event IDs");
            }
            compilation.setEvents(new LinkedHashSet<>(events));
        }
        Optional.ofNullable(updatedCompilation.getPinned()).ifPresent(compilation::setPinned);
        Optional.ofNullable(updatedCompilation.getTitle()).ifPresent(compilation::setTitle);
        CompilationDto compilationDto = compilationMapper.toDto(compilationRepository.save(compilation));
        compilationDto.setEvents(events.stream().map(eventMapper::toShortDto).collect(Collectors.toList()));
        log.info("updated compilation {} was saved successfully", updatedCompilation);
        return compilationDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> search(Boolean pinned, int from, int size) {
        Pageable page = PageRequest.of(from, size);
        List<Compilation> foundCompilations = compilationRepository.search(pinned, page);
        Map<Long, List<EventShortDto>> eventMap = new HashMap<>();
        for (Compilation compilation : foundCompilations) {
            List<EventShortDto> events = new ArrayList<>(
                    compilation.getEvents())
                    .stream()
                    .map(eventMapper::toShortDto)
                    .collect(Collectors.toList());
            eventMap.put(compilation.getId(), events);
        }
        List<CompilationDto> result = foundCompilations
                .stream()
                .map(compilationMapper::toDto)
                .collect(Collectors.toList());
        for (CompilationDto compilationDto : result) {
            compilationDto.setEvents(eventMap.get(compilationDto.getId()));
        }
        log.info("found compilations: {}", foundCompilations);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(Long compilationId) {
        Compilation compilation = findById(compilationId);
        Set<Event> events = compilation.getEvents();
        CompilationDto compilationDto = compilationMapper.toDto(findById(compilationId));
        compilationDto.setEvents(events.stream().map(eventMapper::toShortDto).collect(Collectors.toList()));
        log.info("found compilation: {}", compilationDto);
        return compilationDto;
    }

    private Compilation findById(Long compilationId) {
        return compilationRepository.findById(compilationId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("compilation with id %d ws not found", compilationId)));
    }
}
