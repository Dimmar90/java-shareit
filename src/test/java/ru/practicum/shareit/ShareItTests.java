package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;

import java.sql.SQLException;

@SpringBootTest
class ShareItTests {

    @Mock
    RequestService mockRequestService;

    @Test
    void testAddItemRequest() throws SQLException {
        ItemRequest itemRequestExpected = new ItemRequest();
        ItemRequest itemRequest = new ItemRequest();

        Mockito.when(mockRequestService.addItemRequest(1L, itemRequest))
                .thenReturn(itemRequest);

        Assertions.assertEquals(itemRequestExpected, mockRequestService.addItemRequest(1L, itemRequest));
    }
}
