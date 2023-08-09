package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;

public interface ItemService {
    void addNewItem(Long id, Item item);

    Item updateItem(Long ownerId, Long id, Item item);

    ItemDto getItem(Long ownerId, Long id);

    ArrayList<ItemDto> getUserItems(Long ownerId);

    ArrayList<ItemDto> searchItem(String text);
}
