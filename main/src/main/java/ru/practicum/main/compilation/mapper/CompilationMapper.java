package ru.practicum.main.compilation.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.model.Compilation;

@Component
public class CompilationMapper {
    public Compilation toEntity(NewCompilationDto newCompilation) {
        return Compilation.builder()
                .pinned(newCompilation.getPinned())
                .title(newCompilation.getTitle())
                .build();
    }

    public CompilationDto toDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}
