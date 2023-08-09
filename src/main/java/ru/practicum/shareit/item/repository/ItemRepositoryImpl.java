package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private Map<Long, Item> items;
    private Map<Long, ArrayList<Long>> userItemsIds;
    private Long id;

    public ItemRepositoryImpl(Map<Long, Item> items, Map<Long, ArrayList<Long>> userItemsIds) {
        this.items = items;
        this.userItemsIds = userItemsIds;
    }

    @Override
    public void add(Item item) {
        if (items.isEmpty()) {
            id = 1L;
            item.setId(id);
        } else {
            item.setId(++id);
        }
        items.put(item.getId(), item);
        if (!userItemsIds.keySet().contains(item.getOwner())) {
            ArrayList<Long> itemsId = new ArrayList<>();
            userItemsIds.put(item.getOwner(), itemsId);
            userItemsIds.get(item.getOwner()).add(item.getId());
        } else {
            userItemsIds.get(item.getOwner()).add(item.getId());
        }
    }

    @Override
    public void update(Item item) {
        items.put(item.getId(), item);
    }

    @Override
    public Optional<Item> find(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public ArrayList<Long> findUserItemsIds(Long ownerId) {
        return userItemsIds.get(ownerId);
    }

    @Override
    public Optional<ArrayList<Item>> findItemsBySearch(String searchingText) {
        ArrayList<Item> searchingItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getName().toLowerCase().contains(searchingText.toLowerCase())
                    || item.getDescription().toLowerCase().contains(searchingText.toLowerCase())
                    && item.getAvailable()) {
                searchingItems.add(item);
            }
        }
        return Optional.ofNullable(searchingItems);
    }
}
