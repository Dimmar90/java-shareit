package ru.practicum.shareit.item;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                 @RequestBody Item item) {
        return new ResponseEntity<>(itemService.addItem(ownerId, item), CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                    @PathVariable("id") Long id,
                                    @RequestBody Item item) {
        return new ResponseEntity<>(itemService.updateItem(ownerId, id, item), OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                     @PathVariable("id") Long id) {
        return new ResponseEntity<>(itemService.getItem(ownerId, id), OK);
    }

    @GetMapping
    public ResponseEntity<?> getUserItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return new ResponseEntity<>(itemService.getUserItems(ownerId), OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(value = "text") String searchingText) {
        return new ResponseEntity<>(itemService.searchItem(searchingText), OK);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<?> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable("itemId") Long itemId,
                                        @RequestBody Comment comment) {
        return new ResponseEntity<>(itemService.addComment(userId, itemId, comment), OK);
    }
}
