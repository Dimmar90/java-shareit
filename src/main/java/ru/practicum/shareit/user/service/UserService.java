package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {

    void create(User user);

    void update(User user, Long id);

    Optional<UserDto> getUser(Long id);

    List<UserDto> getAllUsers();

    void delete(Long id);
}
