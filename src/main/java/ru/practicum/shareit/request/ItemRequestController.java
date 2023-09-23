package ru.practicum.shareit.request;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.service.RequestService;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestService requestService;

    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                 @RequestBody ItemRequest itemRequest) {
        return new ResponseEntity<>(requestService.addItemRequest(requesterId, itemRequest), CREATED);
    }

    @GetMapping
    public ResponseEntity<?> get(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return new ResponseEntity<>(requestService.findItemRequests(requesterId), OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getByRequestId(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                            @PathVariable("id") Long requestId) {
        return new ResponseEntity<>(requestService.findRequestById(requesterId, requestId), OK);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllItemRequestWithPageable(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long requesterId,
                                                           @RequestParam(value = "from", required = false) Integer from,
                                                           @RequestParam(value = "size", required = false) Integer size) {
        return new ResponseEntity<>(requestService.findRequestsPageable(requesterId, from, size), OK);
    }
}
