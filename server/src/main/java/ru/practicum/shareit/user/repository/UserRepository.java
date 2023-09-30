package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);

    @Modifying
    @Query("UPDATE User u " +
            "SET u.name = ?1 " +
            "WHERE u.id = ?2")
    void updateName(String name, Long id);

    @Modifying
    @Query("UPDATE User u " +
            "SET u.email = ?1 " +
            "WHERE u.id = ?2")
    void updateEmail(String name, Long id);

    @Query("SELECT name FROM User " +
            "WHERE id = ?1")
    String findNameById(Long id);

    @Query("SELECT email FROM User " +
            "WHERE id = ?1")
    String findEmailById(Long id);

    void deleteById(Long id);
}
