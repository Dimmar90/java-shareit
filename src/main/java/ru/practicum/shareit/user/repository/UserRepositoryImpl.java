package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final HashMap<Long, User> users;
    private final List<String> emails;
    private Long id;

    public UserRepositoryImpl(HashMap<Long, User> users, List<String> emails) {
        this.users = users;
        this.emails = emails;
    }

    @Override
    public void add(User user) {
        if (users.isEmpty()) {
            id = 1L;
            user.setId(id);
        } else {
            user.setId(++id);
        }
        users.put(user.getId(), user);
    }

    @Override
    public void update(User user, Long id) {
        updateUserEmail(user, id);
        user.setId(id);
        if (user.getName() == null) {
            user.setName(users.get(id).getName());
        }
        users.put(id, user);
    }

    @Override
    public Optional<User> find(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return users.values().stream().toList() ;
    }

    @Override
    public void delete(Long id) {
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    public void updateUserEmail(User user, Long id) {
        if (user.getEmail() != null && !user.getEmail().equals(users.get(id).getEmail())) {
            emails.remove(users.get(id).getEmail());
            checkUserEmail(user.getEmail());
        } else if (user.getEmail() == null) {
            user.setEmail(users.get(id).getEmail());
        }
    }

    public void checkUserEmail(String email) {
        if (emails.contains(email)) {
            String message = "Указанный email занят";
            log.error(message);
            throw new AlreadyExistException(message);
        }
        emails.add(email);
    }
}
