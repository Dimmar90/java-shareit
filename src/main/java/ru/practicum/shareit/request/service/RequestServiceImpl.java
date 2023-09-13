package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class RequestServiceImpl implements RequestService {

    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemRequestDtoMapper requestMapper;

    public RequestServiceImpl(ItemRequestRepository itemRequestRepository,
                              UserRepository userRepository,
                              ItemRequestDtoMapper requestMapper) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.requestMapper = requestMapper;
    }

    @Override
    public ItemRequest addItemRequest(Long requesterId, ItemRequest itemRequest) {
        if (itemRequest.getDescription() == null) {
            throw new BadRequestException("Отсутствует описание запроса");
        }

        User requester = userRepository
                .findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id: " + requesterId));

        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequesterId(requesterId);

        itemRequestRepository.save(itemRequest);
        return itemRequest;
    }

    @Override
    public List<ItemRequestDto> findItemRequests(Long requesterId) {
        User requester = userRepository
                .findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id: " + requesterId));

        List<ItemRequestDto> itemsRequests = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequestRepository.findItemRequestsOfUser(requesterId)) {
            itemRequest.setRequester(requester);
            itemRequest.setItems(itemRequestRepository.findRequestItemsList(itemRequest.getId()));
            itemsRequests.add(requestMapper.toItemRequestDto(itemRequest));
        }

        return itemsRequests;
    }

    @Override
    public ItemRequestDto findRequestById(Long requesterId, Long requestId) {
        ItemRequest itemRequest = itemRequestRepository
                .findRequestItemById(requestId)
                .orElseThrow(() -> new NotFoundException("Не найден запрос на вещь id: " + requesterId));

        User requester = userRepository
                .findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id: " + requesterId));

        itemRequest.setItems(itemRequestRepository.findRequestItemsList(requestId));

        return requestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> findRequestsPageable(Long requesterId, Integer from, Integer size) {
        Pageable pageableSize;

        if (from == null || size == null) {
            return findAllRequests(itemRequestRepository.findAllItemRequests());
        } else {
            pageableSize = PageRequest.of(from, size);
        }

        if (from < 0 || size < 0) {
            throw new BadRequestException("Указаны не верные данный нахождения запросов");
        }

        if (requesterId != null) {
            User requester = userRepository
                    .findById(requesterId)
                    .orElseThrow(() -> new NotFoundException("Не найден пользователь id: " + requesterId));
            return findAllRequests(itemRequestRepository.findItemRequestsOfOtherUsersPageable(pageableSize, requesterId).toList());

        } else {
            return findAllRequests(itemRequestRepository.findItemRequestsPageable(pageableSize).toList());
        }
    }

    private List<ItemRequestDto> findAllRequests(List<ItemRequest> itemRequestList) {
        List<ItemRequestDto> requestsList = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequestList) {
            itemRequest.setRequester(userRepository.findById(itemRequest.getRequesterId()).get());
            itemRequest.setItems(itemRequestRepository.findRequestItemsList(itemRequest.getId()));
            requestsList.add(requestMapper.toItemRequestDto(itemRequest));
        }

        return requestsList;
    }
}
