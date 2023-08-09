package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Optional;

public interface ItemRepository {

    void add(Item item);

    void update(Item item);

    Optional<Item> find(Long id);

    ArrayList<Long> findUserItemsIds(Long ownerId);

    Optional<ArrayList<Item>> findItemsBySearch(String text);
}
