package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    private final BookingMapper bookingMapper = new BookingMapper();

    public User createBooker() {
        User booker = new User();
        booker.setId(1L);
        booker.setName("bookerName");
        booker.setEmail("booker@email");
        return booker;
    }

    public Item createNewItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setOwner(2L);
        return item;
    }

    public Booking createExpectedBooking() {
        Booking expectedBooking = new Booking();
        expectedBooking.setId(1L);
        expectedBooking.setItemId(1L);
        expectedBooking.setStart(LocalDateTime.of(2023, 10, 20, 10, 00));
        expectedBooking.setEnd(LocalDateTime.of(2023, 10, 21, 10, 00));
        expectedBooking.setBooker(createBooker());
        expectedBooking.setItem(createNewItem());
        expectedBooking.setStatus(BookingStatus.WAITING);
        return expectedBooking;
    }

    @Test
    @SneakyThrows
    void addTest() {
        Booking booking = createExpectedBooking();
        when(bookingService.addBooking(booking.getBooker().getId(), booking)).thenReturn(booking);

        String result = mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", booking.getBooker().getId())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(booking)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(booking), result);
    }

    @Test
    @SneakyThrows
    void approved() {
        Booking booking = createExpectedBooking();
        when(bookingService.approved(booking.getBooker().getId(), booking.getId(), false))
                .thenReturn(booking);

        String result = mvc.perform(patch("/bookings/{id}?approved=false", booking.getId())
                        .header("X-Sharer-User-Id", booking.getBooker().getId())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(booking), result);
    }

    @Test
    @SneakyThrows
    void get() {
        BookingDto booking = bookingMapper.toBookingDto(createExpectedBooking());
        when(bookingService.findBooking(booking.getBooker().getId(), booking.getId()))
                .thenReturn(booking);

        String result = mvc.perform(MockMvcRequestBuilders.get("/bookings/{id}", booking.getId())
                        .header("X-Sharer-User-Id", booking.getBooker().getId())
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(booking), result);
    }
}

