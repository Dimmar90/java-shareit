package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                      @RequestBody ItemRequestDto itemRequest) {
        if (itemRequest.getDescription() == null) {
            throw new BadRequestException("Not find request description");
        }
        return requestClient.addRequest(requesterId, itemRequest);
    }

    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return requestClient.getResponses(requesterId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getByRequestId(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                            @PathVariable("id") Long requestId) {
        return requestClient.getByRequestId(requestId, requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllItemRequestWithPageable(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long requesterId,
                                                           @RequestParam(value = "from", required = false) Integer from,
                                                           @RequestParam(value = "size", required = false) Integer size) {
        if (from == null) {
            return requestClient.getAllItemRequestsOfRequester(requesterId);
        }
        return requestClient.getAllItemRequestsOfRequesterPageable(requesterId, from, size);
    }
}
