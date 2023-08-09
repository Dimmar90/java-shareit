package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

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
    public void update(User user, Long id) {
        if (userRepository.find(id).isPresent()) {
            userRepository.update(user, id);
            log.info("Обновлен пользователь: {}", user);
        } else {
            String message = "Пользователь не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public Optional<UserDto> getUser(Long id) {
        if (userRepository.find(id).isPresent()) {
            UserDto user = mapper.toUserDto(userRepository.find(id).get());
            log.info("Найден пользователь: {}", user);
            return Optional.ofNullable(user);
        } else {
            String message = "Пользователь не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        if (userRepository.findAll().isPresent()) {
            log.info("Найдены все пользователи");
            return userRepository.findAll().get().stream().map(mapper::toUserDto).collect(toList());
        } else {
            String message = "Пользователи не найдены";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public void delete(Long id) {
        if (userRepository.find(id).isPresent()) {
            log.info("Удален пользователь: {}", userRepository.find(id));
            userRepository.delete(id);
        } else {
            throw new NotFoundException("Id пользователя: " + id + " не найден");
        }
    }
}
