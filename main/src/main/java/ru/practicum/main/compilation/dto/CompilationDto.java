package ru.practicum.main.compilation.dto;

import lombok.*;
import ru.practicum.main.event.dto.EventShortDto;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private List<EventShortDto> events;
    @NotNull
    private Long id;
    @NotNull
    private Boolean pinned;
    @NotNull
    private String title;
}
