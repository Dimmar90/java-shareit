package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserRepository {

    void add(User user);

    void update(User user, Long id);

    Optional<User> find(Long id);

    Optional<Collection<User>> findAll();

    void delete(Long id);
}
