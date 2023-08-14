package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper mapper;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, ItemMapper mapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public Item addItem(Long ownerId, Item item) {
        User user = userRepository.find(ownerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id: " + ownerId));
        validateItem(item);
        item.setOwner(ownerId);
        itemRepository.add(item);
        log.info("Добавлена вещь: {}", item);
        return item;
    }

    @Override
    public Item updateItem(Long ownerId, Long id, Item item) {
        if (itemRepository.find(id).isPresent() && Objects.equals(itemRepository.find(id).get().getOwner(), ownerId)) {
            Item itemToUpdate = itemRepository.find(id).get();
            updatedItemFields(itemToUpdate, item);
            itemRepository.update(itemToUpdate);
            log.info("Обновлена вещь: {}", itemToUpdate);
            return itemToUpdate;
        } else {
            String message = "У пользователя нет доступа к обновлению вещи";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public ItemDto getItem(Long ownerId, Long id) {
        Item item = itemRepository.find(id).orElseThrow(() -> new NotFoundException("Не найдена вещь id: " + id));
        log.info("Найденная вещь: {}", mapper.toItemDto(item));
        return mapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getUserItems(Long ownerId) {
        User user = userRepository.find(ownerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id: " + ownerId));
        List<ItemDto> userItems = new ArrayList<>();
        for (Long id : itemRepository.findUserItemsIds(ownerId)) {
            Item item = itemRepository.find(id).get();
            ItemDto itemDto = mapper.toItemDto(item);
            userItems.add(itemDto);
        }
        log.info("Список вещей пользователя : {}", userItems);
        return userItems;
    }

    @Override
    public List<ItemDto> searchItem(String searchingText) {
        List<ItemDto> searchingItems = new ArrayList<>();
        if (searchingText.isEmpty()) {
            log.info("Отсутствует запрос на поиск вещи");
            return searchingItems;
        }
        for (Item item : itemRepository.findItemsBySearch(searchingText)) {
            ItemDto searchingItem = mapper.toItemDto(item);
            searchingItems.add(searchingItem);
        }
        log.info("Найденные вещи по запросу {} : {}", searchingText, searchingItems);
        return searchingItems;
    }

    private void validateItem(Item item) {
        if (item.getAvailable() == null) {
            String message = "Доступность вещи не найдена";
            log.warn(message);
            throw new BadRequestException(message);
        }
        if (item.getName().isEmpty() || item.getName().isBlank()) {
            String message = "Наименование вещи не найдено";
            log.warn(message);
            throw new BadRequestException(message);
        }
        if (item.getDescription() == null) {
            String message = "Описание вещи не найдено";
            log.warn(message);
            throw new BadRequestException(message);
        }
    }

    private void updatedItemFields(Item itemToUpdate, Item item) {
        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
    }
}
