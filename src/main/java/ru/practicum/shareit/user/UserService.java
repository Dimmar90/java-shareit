package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(User user) {
        userRepository.add(user);
    }

    public void updateUser(User user, Long id) {
        userRepository.update(user, id);
    }

    public User getUser(Long id) {
        if (userRepository.find(id).isEmpty()) {
            String message = "Пользователь не найден";
            log.error(message);
            throw new NotFoundException(message);
        } else {
            log.info("Найден: {}", userRepository.find(id).get());
            return userRepository.find(id).get();
        }
    }

    public Collection<User> getAllUsers() {
        if (userRepository.findAll().isEmpty()) {
            String message = "Пользователи не найдены";
            log.error(message);
            throw new NotFoundException(message);
        } else {
            return userRepository.findAll().get();
        }
    }

    public void deleteUser(Long id) {
        userRepository.delete(id);
    }
}
