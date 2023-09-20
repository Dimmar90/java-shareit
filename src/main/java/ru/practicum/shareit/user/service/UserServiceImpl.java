package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public User create(User user) {
        if (user.getEmail() == null) {
            String message = "User email is absent";
            log.error(message);
            throw new BadRequestException(message);
        }
        userRepository.save(user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User userUpdated, Long id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id: " + id));
        userUpdated.setId(id);
        if (userUpdated.getName() != null) {
            userRepository.updateName(userUpdated.getName(), id);
        } else {
            userUpdated.setName(userRepository.findNameById(id));
        }
        if (userUpdated.getEmail() != null) {
            userRepository.updateEmail(userUpdated.getEmail(), id);
        } else {
            userUpdated.setEmail(userRepository.findEmailById(id));
        }
        log.info("Обновлен пользователь: {}", user);
        return userUpdated;
    }

    @Override
    public UserDto getUser(Long id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id: " + id));
        UserDto userDto = mapper.toUserDto(user);
        log.info("Найден пользователь: {}", userDto);
        return userDto;
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> allUsers = userRepository
                .findAll()
                .stream()
                .map(mapper::toUserDto).collect(toList());
        log.info("Найдены все пользователи: {}", allUsers);
        return allUsers;
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Не найден пользователь id: " + id));
        log.info("Удален пользователь: {}", user);
        userRepository.deleteById(id);
    }
}
