package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Objects;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private ItemMapper mapper;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, ItemMapper mapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public void addNewItem(Long ownerId, Item item) {
        checkItem(ownerId, item);
        item.setOwner(ownerId);
        itemRepository.add(item);
        log.info("Добавлена вещь: {}", item);
    }

    @Override
    public Item updateItem(Long ownerId, Long id, Item item) {
        if (Objects.equals(itemRepository.find(id).get().getOwner(), ownerId)) {
            Item itemToUpdate = itemRepository.find(id).get();
            checkUpdatedItem(itemToUpdate, item);
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
        if (itemRepository.find(id).isPresent()) {
            ItemDto item = mapper.toItemDto(itemRepository.find(id).get());
            log.info("Найденная вещь: {}", item);
            return item;
        } else {
            String message = "Вещь не найдена";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    public ArrayList<ItemDto> getUserItems(Long ownerId) {
        if (userRepository.find(ownerId).isPresent()) {
            ArrayList<ItemDto> userItems = new ArrayList<>();
            for (Long id : itemRepository.findUserItemsIds(ownerId)) {
                ItemDto item = mapper.toItemDto(itemRepository.find(id).get());
                userItems.add(item);
            }
            log.info("Список вещей пользователя : {}", userItems);
            return userItems;
        } else {
            String message = "id пользователя не найден";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    public ArrayList<ItemDto> searchItem(String searchingText) {
        ArrayList<ItemDto> searchingItems = new ArrayList<>();

        if (searchingText.isEmpty()) {
            log.info("Отсутствует запрос на поиск вещи", searchingText);
            return searchingItems;
        }

        if (itemRepository.findItemsBySearch(searchingText).isPresent()) {
            for (Item item : itemRepository.findItemsBySearch(searchingText).get()) {
                ItemDto searchingItem = mapper.toItemDto(item);
                searchingItems.add(searchingItem);
            }
            log.info("Найденные вещи по запросу {} : {}", searchingText, searchingItems);
        }

        return searchingItems;
    }

    public void checkItem(Long ownerId, Item item) {
        if (userRepository.find(ownerId).isEmpty()) {
            String message = "Id владельца не найден";
            log.warn(message);
            throw new NotFoundException(message);
        }
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

    public void checkUpdatedItem(Item itemToUpdate, Item updatedItem) {
        if (updatedItem.getName() != null) {
            itemToUpdate.setName(updatedItem.getName());
        }
        if (updatedItem.getDescription() != null) {
            itemToUpdate.setDescription(updatedItem.getDescription());
        }
        if (updatedItem.getAvailable() != null) {
            itemToUpdate.setAvailable(updatedItem.getAvailable());
        }
    }
}
