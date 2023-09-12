package ru.practicum.main.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.model.User;

@Component
public class UserMapper {
    public User toEntity(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
