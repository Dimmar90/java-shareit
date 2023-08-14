package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    void add(Item item);

    void update(Item item);

    Optional<Item> find(Long id);

    List<Long> findUserItemsIds(Long ownerId);

    List<Item> findItemsBySearch(String text);
}
