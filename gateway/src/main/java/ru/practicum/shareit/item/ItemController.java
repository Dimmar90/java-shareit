package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<?> add(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                 @RequestBody ItemDto item) {
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
        return itemClient.addItem(ownerId, item);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                    @PathVariable("id") Long itemId,
                                    @RequestBody ItemDto item) {
        return itemClient.updateItem(itemId, ownerId, item);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                     @PathVariable("id") Long itemId) {
        return itemClient.getItem(itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<?> getUserItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemClient.getUserItems(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(value = "text") String searchingText) {
        return itemClient.search(searchingText);
    }
}
