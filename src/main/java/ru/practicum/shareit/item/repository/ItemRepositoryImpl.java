package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private static List<Item> items;

    @Override
    public void add(Item item) {
        items.add(item);
    }

    @Override
    public List<Item> returnAll() {
        return items;
    }
}
