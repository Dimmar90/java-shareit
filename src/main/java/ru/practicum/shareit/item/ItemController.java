package ru.practicum.shareit.item;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestHeader("X-Sharer-User-Id") Long ownerId, @RequestBody Item item) {
        itemService.addNewItem(ownerId, item);
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable("id") Long id, @RequestBody Item item) {
        return new ResponseEntity<>(itemService.updateItem(ownerId, id, item), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItem(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable("id") Long id) {
        return new ResponseEntity<>(itemService.getItem(ownerId, id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getUserItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return new ResponseEntity<>(itemService.getUserItems(ownerId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(value = "text") String text) {
        return new ResponseEntity<>(itemService.searchItem(text), HttpStatus.OK);
    }
}
