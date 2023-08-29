package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findById(Long id);

    @Query("select name from Item i where i.id = ?1")
    String findNameById(Long id);

    @Query("select owner from Item i where i.id = ?1")
    Long findOwnerById(Long id);

    @Modifying
    @Query("update Item i set i.name = ?1 where i.id = ?2")
    void updateName(String name, Long id);

    @Query("select description from Item i where i.id = ?1")
    String findDescriptionById(Long id);

    @Modifying
    @Query("update Item i set i.description = ?1 where i.id = ?2")
    void updateDescription(String name, Long id);

    @Query("select available from Item i where i.id = ?1")
    Boolean findAvailableById(Long id);

    @Modifying
    @Query("update Item i set i.available = ?1 where i.id = ?2")
    void updateAvailable(Boolean available, Long id);

    @Query("select id from Item i where i.owner = ?1")
    List<Long> findIdByOwner(Long owner);

    @Query("select i from Item i where lower (i.name) like concat ('%',?1,'%') " +
            "or lower (i.description) like concat ('%',?1,'%') and i.available = true")
    List<Item> searchItemByNameOrDescription(String searchingText);
}
