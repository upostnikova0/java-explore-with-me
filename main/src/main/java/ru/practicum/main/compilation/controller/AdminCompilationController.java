package ru.practicum.main.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto newCompilation) {
        log.info("POST api from admin: {}", newCompilation);
        return compilationService.addCompilation(newCompilation);
    }

    @DeleteMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @Positive Long compilationId) {
        log.info("DELETE api from admin {}", compilationId);
        compilationService.deleteCompilation(compilationId);
    }

    @PatchMapping("/{compilationId}")
    public CompilationDto updateCompilation(@RequestBody @Valid UpdateCompilationRequest updatedCompilation,
                                            @PathVariable @Positive Long compilationId) {
        log.info("PATCH api from admin {}, {}", updatedCompilation, compilationId);
        return compilationService.updateCompilation(compilationId, updatedCompilation);
    }
}
