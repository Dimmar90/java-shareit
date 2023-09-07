package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findById(Long id);

    @Query("SELECT name FROM Item i " +
            "WHERE i.id = ?1")
    String findNameById(Long id);

    @Query("SELECT owner FROM Item i " +
            "WHERE i.id = ?1")
    Long findOwnerById(Long id);

    @Modifying
    @Query("UPDATE Item i " +
            "SET i.name = ?1 " +
            "WHERE i.id = ?2")
    void updateName(String name, Long id);

    @Query("SELECT description FROM Item i " +
            "WHERE i.id = ?1")
    String findDescriptionById(Long id);

    @Modifying
    @Query("UPDATE Item i " +
            "SET i.description = ?1 " +
            "WHERE i.id = ?2")
    void updateDescription(String name, Long id);

    @Query("SELECT available FROM Item i " +
            "WHERE i.id = ?1")
    Boolean findAvailableById(Long id);

    @Modifying
    @Query("UPDATE Item i SET i.available = ?1 WHERE i.id = ?2")
    void updateAvailable(Boolean available, Long id);

    @Query("SELECT id FROM Item i WHERE i.owner = ?1")
    List<Long> findIdByOwner(Long owner);

    @Query("SELECT i FROM Item i " +
            "WHERE LOWER (i.name) LIKE CONCAT ('%',?1,'%') " +
            "OR LOWER (i.description) LIKE CONCAT ('%',?1,'%') AND i.available = TRUE")
    List<Item> searchItemByNameOrDescription(String searchingText);
}
