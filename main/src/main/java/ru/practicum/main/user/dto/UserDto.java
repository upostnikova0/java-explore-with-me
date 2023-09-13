package ru.practicum.main.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank
    @NotNull
    @Size(min = 2, max = 250)
    private String name;
    @NotBlank
    @NotNull
    @Email
    @Size(min = 6, max = 254)
    private String email;
}
