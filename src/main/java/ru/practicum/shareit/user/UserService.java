package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    public UserService(UserRepository userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

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

    public void update(User user, Long id) {
        if (userRepository.find(id).isPresent()) {
            userRepository.update(user, id);
            log.info("Обновлен пользователь: {}", user);
        } else {
            String message = "Id пользователя не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    public Optional<UserDto> getUser(Long id) {
        if (userRepository.find(id).isEmpty()) {
            String message = "Пользователь не найден";
            log.error(message);
            throw new NotFoundException(message);
        } else {
            UserDto user = mapper.toUserDto(userRepository.find(id).get());
            log.info("Найден пользователь: {}", user);
            return Optional.ofNullable(user);
        }
    }

    public List<UserDto> getAllUsers() {
        if (userRepository.findAll().isEmpty()) {
            String message = "Пользователи не найдены";
            log.error(message);
            throw new NotFoundException(message);
        } else {
            return userRepository.findAll().get().stream().map(mapper::toUserDto).collect(toList());
        }
    }

    public void delete(Long id) {
        if (userRepository.find(id).isPresent()) {
            log.info("Удален пользователь: {}", userRepository.find(id));
            userRepository.delete(id);
        } else {
            throw new NotFoundException("Id пользователя: " + id + " не найден");
        }
    }
}
