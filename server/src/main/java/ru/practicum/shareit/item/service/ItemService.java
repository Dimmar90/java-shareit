package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(Long id, Item item);

    Item updateItem(Long ownerId, Long id, Item item);

    ItemDto getItem(Long ownerId, Long id);

    List<ItemDto> getUserItems(Long ownerId);

    List<ItemDto> searchItem(String text);

    Comment addComment(Long userId, Long itemId, Comment comment);
}
