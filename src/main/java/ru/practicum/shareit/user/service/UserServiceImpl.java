package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public void create(User user) {
        if (user.getEmail() == null) {
            String message = "Отсутствует email пользователя";
            log.error(message);
            throw new BadRequestException(message);
        }
        userRepository.checkUserEmail(user.getEmail());
        userRepository.add(user);
        log.info("Добавлен пользователь: {}", user);
    }

    @Override
    public void update(User userUpdated, Long id) {
        User user = userRepository.find(id).orElseThrow(() -> new NotFoundException("Не найден пользователь id: " + id));
        userRepository.update(userUpdated, id);
        log.info("Обновлен пользователь: {}", user);
    }

    @Override
    public UserDto getUser(Long id) {
        User user = userRepository.find(id).orElseThrow(() -> new NotFoundException("Не найден пользователь id: " + id));
        UserDto userDto = mapper.toUserDto(user);
        log.info("Найден пользователь: {}", userDto);
        return userDto;
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> allUsers = userRepository.findAll().stream().map(mapper::toUserDto).toList();
        log.info("Найдены все пользователи: {}", allUsers);
        return allUsers;
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.find(id).orElseThrow(() -> new NotFoundException("Не найден пользователь id: " + id));
        log.info("Удален пользователь: {}", user);
        userRepository.delete(id);
    }
}
