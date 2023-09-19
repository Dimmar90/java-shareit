package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper mapper;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository, ItemMapper mapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.mapper = mapper;
    }

    @Override
    public Item addItem(Long ownerId, Item item) {
        validateItem(item);
        User user = userRepository
                .findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id: " + ownerId));
        item.setOwner(ownerId);
        itemRepository.save(item);
        log.info("Добавлена вещь: {}", item);
        return item;
    }

    @Override
    public Item updateItem(Long ownerId, Long id, Item updatedItem) {
        if (itemRepository.findById(id).isPresent() && Objects.equals(itemRepository.findOwnerById(id), ownerId)) {
            updatedItem.setId(id);
            if (updatedItem.getName() != null) {
                itemRepository.updateName(updatedItem.getName(), id);
            } else {
                updatedItem.setName(itemRepository.findNameById(id));
            }
            if (updatedItem.getDescription() != null) {
                itemRepository.updateDescription(updatedItem.getDescription(), id);
            } else {
                updatedItem.setDescription(itemRepository.findDescriptionById(id));
            }
            if (updatedItem.getAvailable() != null) {
                itemRepository.updateAvailable(updatedItem.getAvailable(), id);
            } else {
                updatedItem.setAvailable(itemRepository.findAvailableById(id));
            }
            log.info("Обновлена вещь: {}", updatedItem);
            return updatedItem;
        } else {
            String message = "User has not access to update item";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    @Override
    public ItemDto getItem(Long ownerId, Long id) {
        Item item = itemRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь id: " + id));
        if (!bookingRepository.findLastItemBooking(id, ownerId).isEmpty()
                && bookingRepository.findLastItemBooking(id, ownerId).get(0).getStatus() != BookingStatus.REJECTED) {
            item.setLastBooking(bookingRepository.findLastItemBooking(id, ownerId).get(0));
        }
        if (!bookingRepository.findNextItemBooking(id, ownerId).isEmpty()
                && bookingRepository.findNextItemBooking(id, ownerId).get(0).getStatus() != BookingStatus.REJECTED) {
            item.setNextBooking(bookingRepository.findNextItemBooking(id, ownerId).get(0));
        }
        item.setComments(commentRepository.findCommentByItemId(id));
        log.info("Найденная вещь: {}", mapper.toItemDto(item));
        return mapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getUserItems(Long ownerId) {
        User user = userRepository
                .findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id: " + ownerId));
        List<ItemDto> userItems = new ArrayList<>();
        for (Long id : itemRepository.findIdByOwner(ownerId)) {
            Item item = itemRepository.findById(id).get();
            if (!bookingRepository.findLastItemBooking(id, ownerId).isEmpty()
                    && bookingRepository.findLastItemBooking(id, ownerId).get(0).getStatus() != BookingStatus.REJECTED) {
                item.setLastBooking(bookingRepository.findLastItemBooking(id, ownerId).get(0));
            }
            if (!bookingRepository.findNextItemBooking(id, ownerId).isEmpty()
                    && bookingRepository.findNextItemBooking(id, ownerId).get(0).getStatus() != BookingStatus.REJECTED) {
                item.setNextBooking(bookingRepository.findNextItemBooking(id, ownerId).get(0));
            }
            item.setComments(commentRepository.findCommentByItemId(id));
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
        for (Item item : itemRepository.searchItemByNameOrDescription(searchingText.toLowerCase())) {
            item.setComments(commentRepository.findCommentByItemId(item.getId()));
            ItemDto searchingItem = mapper.toItemDto(item);
            searchingItems.add(searchingItem);
        }
        log.info("Найденные вещи по запросу {} : {}", searchingText, searchingItems);
        return searchingItems;
    }

    public void validateItem(Item item) {
        if (item.getAvailable() == null) {
            String message = "Not found item available";
            log.warn(message);
            throw new BadRequestException(message);
        }
        if (item.getName().isEmpty() || item.getName().isBlank()) {
            String message = "Not found item name";
            log.warn(message);
            throw new BadRequestException(message);
        }
        if (item.getDescription() == null) {
            String message = "Not found item description";
            log.warn(message);
            throw new BadRequestException(message);
        }
    }

    @Override
    public Comment addComment(Long userId, Long itemId, Comment comment) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id: " + userId));
        Item item = itemRepository
                .findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь id: " + itemId));
        if (comment.getText().isBlank() || comment.getText().isEmpty()) {
            throw new BadRequestException("Комментарий не может быть пустым");
        }
        if (bookingRepository.countUserBookingsOfItem(userId, itemId) != 0) {
            comment.setAuthorName(user.getName());
            comment.setCreated(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
            comment.setItemId(itemId);
            commentRepository.save(comment);
            return comment;
        } else {
            throw new BadRequestException("Пользователь не может оставить комментарий");
        }
    }
}
