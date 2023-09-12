package ru.practicum.main.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.mapper.UserMapper;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<User> users;
        if (ids != null) {
            users = userRepository.findAllByIdIn(ids, PageRequest.of(from / size, size));
        } else {
            users = userRepository.findAll(PageRequest.of(from / size, size)).getContent();
        }
        log.info("found users: {}", users);
        return users.isEmpty()
                ? new ArrayList<>()
                : users.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        UserDto savedUser = userMapper.toDto(userRepository.save(user));
        log.info("new user was added to db: {}", savedUser);
        return savedUser;
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        User user = findById(userId);
        log.info("user with id {} was deleted from db", userId);
        userRepository.deleteById(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("user with id %d was not found", userId)));
    }
}
