package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ItemRequestController.class)
class ShareItTests {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    RequestService requestService;

    @Autowired
    private MockMvc mvc;

    private ItemRequest itemRequest = new ItemRequest();

    private ItemRequestDtoMapper requestDtoMapper = new ItemRequestDtoMapper();

    @BeforeEach
    void setRequestParameters() {
        itemRequest.setId(1L);
        itemRequest.setDescription("Request Description");
    }

    @Test
    void addNewItemRequestTest() throws Exception {
        when(requestService.addItemRequest(anyLong(), any()))
                .thenReturn(itemRequest);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Assertions.assertEquals(1L, itemRequest.getId());
        Assertions.assertEquals("Request Description", itemRequest.getDescription());
    }

    @Test
    void findItemRequestsTest() throws Exception {
        requestService.addItemRequest(1L, itemRequest);
        List<ItemRequestDto> itemRequests = new ArrayList<>();
        itemRequests.add(requestDtoMapper.toItemRequestDto(itemRequest));

        when(requestService.findItemRequests(anyLong()))
                .thenReturn(itemRequests);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Assertions.assertEquals(1L, itemRequests.get(0).getId());
        Assertions.assertEquals("Request Description", itemRequests.get(0).getDescription());
    }
}
