package ru.practicum.main.event.location.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    @NotNull
    private float lat;
    @NotNull
    private float lon;
}
