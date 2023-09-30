package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequest addItemRequest(Long requesterId, ItemRequest itemRequest);

    List<ItemRequestDto> findItemRequests(Long requesterId);

    ItemRequestDto findRequestById(Long requesterId, Long requestId);

    List<ItemRequestDto> findRequestsPageable(Long requesterId, Integer from, Integer size);
}
