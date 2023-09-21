package ru.practicum.shareit;

import org.apache.coyote.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {
    @InjectMocks
    private RequestServiceImpl requestService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Spy
    private ItemRequestDtoMapper itemRequestDtoMapper;

    private ItemRequest createRequest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("requestDescription");
        itemRequest.setRequester(createRequester());
        List<Item> items = new ArrayList<>();
        itemRequest.setItems(items);

        return itemRequest;
    }

    private List<ItemRequestDto> createItemRequestDtoList(List<ItemRequest> itemRequestList) {
        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestList) {
            itemRequestDtoList.add(itemRequestDtoMapper.toItemRequestDto(itemRequest));
        }
        return itemRequestDtoList;
    }

    private User createRequester() {
        User requester = new User();
        requester.setId(1L);
        requester.setName("ownerName");
        requester.setEmail("owner@email");
        return requester;
    }

    private Pageable createPageRequestUsing(int page, int size) {
        return PageRequest.of(page, size);
    }

    private Page<ItemRequest> getRequests(int page, int size) {
        Pageable pageRequest = createPageRequestUsing(page, size);
        List<ItemRequest> allItemRequests = new ArrayList<>();
        allItemRequests.add(createRequest());
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), allItemRequests.size());
        List<ItemRequest> pageContent = allItemRequests.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, allItemRequests.size());
    }

    @Test
    void addItemRequestTest() {
        ItemRequest expectedItemRequest = createRequest();
        User requester = createRequester();
        Mockito.when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        Mockito.when(itemRequestRepository.save(expectedItemRequest)).thenReturn(expectedItemRequest);

        ItemRequest actualItemRequest = requestService.addItemRequest(requester.getId(), expectedItemRequest);
        assertEquals(expectedItemRequest, actualItemRequest);
    }

    @Test
    void addItemRequestWithEmptyDescriptionTest() {
        ItemRequest expectedItemRequest = createRequest();
        expectedItemRequest.setDescription(null);
        User requester = createRequester();

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> requestService.addItemRequest(requester.getId(), expectedItemRequest)
        );

        assertEquals("Not find request description", exception.getMessage());
    }

    @Test
    void findItemRequestsTest() {
        ItemRequest itemRequest = createRequest();
        User requester = createRequester();
        List<ItemRequest> itemRequestList = new ArrayList<>();
        itemRequestList.add(itemRequest);
        List<ItemRequestDto> expectedItemRequestList = createItemRequestDtoList(itemRequestList);
        Mockito.when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        Mockito.when(itemRequestRepository.findItemRequestsOfUser(requester.getId())).thenReturn(itemRequestList);

        List<ItemRequestDto> actualItemRequestList = requestService.findItemRequests(requester.getId());

        assertEquals(expectedItemRequestList, actualItemRequestList);
    }

    @Test
    void findRequestByIdTest() {
        ItemRequest itemRequest = createRequest();
        User requester = createRequester();
        ItemRequestDto expectingItemRequest = itemRequestDtoMapper.toItemRequestDto(itemRequest);
        Mockito.when(itemRequestRepository.findRequestItemById(requester.getId())).thenReturn(Optional.of(itemRequest));
        Mockito.when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));

        ItemRequestDto actualItemRequest = requestService.findRequestById(requester.getId(), itemRequest.getId());

        assertEquals(expectingItemRequest, actualItemRequest);
    }

    @Test
    void findRequestsPageableTest() {
        ItemRequest itemRequest = createRequest();
        User requester = createRequester();
        Pageable pageableSize = createPageRequestUsing(0, 1);
        List<ItemRequestDto> expectedItemRequest = new ArrayList<>();
        expectedItemRequest.add(itemRequestDtoMapper.toItemRequestDto(itemRequest));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(requester));
        Mockito.when(itemRequestRepository.findItemRequestsOfOtherUsersPageable(pageableSize, requester.getId())).thenReturn(getRequests(0, 1));

        List<ItemRequestDto> actualRequestList = requestService.findRequestsPageable(requester.getId(), 0, 1);

        assertEquals(expectedItemRequest, actualRequestList);
    }

    @Test
    void findAllRequestsWithoutPageableTest() {
        ItemRequest itemRequest = createRequest();
        User requester = createRequester();
        List<ItemRequest> requestList = new ArrayList<>();
        requestList.add(itemRequest);
        List<ItemRequestDto> expectedItemRequest = new ArrayList<>();
        expectedItemRequest.add(itemRequestDtoMapper.toItemRequestDto(requestList.get(0)));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(requester));
        Mockito.when(itemRequestRepository.findAllItemRequests()).thenReturn(requestList);

        List<ItemRequestDto> actualRequestList = requestService.findRequestsPageable(requester.getId(), null, null);

        assertEquals(expectedItemRequest, actualRequestList);
    }
}
