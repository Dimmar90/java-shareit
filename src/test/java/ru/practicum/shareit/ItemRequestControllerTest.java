package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.RequestService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RequestService requestService;

    @MockBean
    private ItemRequestRepository itemRequestRepository;

    private final ItemRequestDtoMapper requestDtoMapper = new ItemRequestDtoMapper();


    public ItemRequest createItemRequest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Request Description");
        return itemRequest;
    }

    @Test
    @SneakyThrows
    void addNewItemRequestTest() {
        ItemRequest expectedItemRequest = createItemRequest();
        Long requesterId = 1L;
        when(requestService.addItemRequest(requesterId, expectedItemRequest)).thenReturn(expectedItemRequest);

        String result = mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", requesterId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(expectedItemRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println(result);
        assertEquals(mapper.writeValueAsString(expectedItemRequest), result);
    }

    @Test
    @SneakyThrows
    void findItemRequests() {
        ItemRequestDto itemRequestDto = requestDtoMapper.toItemRequestDto(createItemRequest());
        Long requesterId = 1L;
        List<ItemRequestDto> expectedItemRequestDtoList = new ArrayList<>();
        expectedItemRequestDtoList.add(itemRequestDto);
        when(requestService.findItemRequests(requesterId)).thenReturn(expectedItemRequestDtoList);

        String result = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", requesterId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(expectedItemRequestDtoList)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(expectedItemRequestDtoList), result);
    }

    @Test
    @SneakyThrows
    void findItemRequestById() {
        ItemRequestDto expectedItemRequestDto = requestDtoMapper.toItemRequestDto(createItemRequest());
        Long requesterId = 1L;
        Long requestId = 1L;
        when(requestService.findRequestById(requesterId, requestId)).thenReturn(expectedItemRequestDto);

        String result = mvc.perform(get("/requests/{id}", requestId)
                        .header("X-Sharer-User-Id", requesterId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(expectedItemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(expectedItemRequestDto), result);
    }


    @Test
    @SneakyThrows
    void getAllItemRequestWithPageable() {
        ItemRequestDto itemRequestDto = requestDtoMapper.toItemRequestDto(createItemRequest());
        Long requesterId = 1L;
        List<ItemRequestDto> expectedItemRequestDtoList = new ArrayList<>();
        expectedItemRequestDtoList.add(itemRequestDto);
        when(requestService.findRequestsPageable(requesterId, 0, 2)).thenReturn(expectedItemRequestDtoList);

        String result = mvc.perform(get("/requests/all?from=0&size=2")
                        .header("X-Sharer-User-Id", requesterId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(expectedItemRequestDtoList)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(expectedItemRequestDtoList), result);
    }
}

