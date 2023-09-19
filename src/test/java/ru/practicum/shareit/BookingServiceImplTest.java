package ru.practicum.shareit;


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
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingServiceImpl;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private BookingMapper bookingMapper;


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

    private Booking createExpectedBooking() {
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

    private List<BookingDto> createExpectedListOfBookingDto() {
        List<BookingDto> expectedListOfBookingDto = new ArrayList<>();
        expectedListOfBookingDto.add(bookingMapper.toBookingDto(createExpectedBooking()));
        return expectedListOfBookingDto;
    }

    private Pageable createPageRequestUsing(int page, int size) {
        return PageRequest.of(page, size);
    }

    private Page<Booking> getBookings(int page, int size) {
        Pageable pageRequest = createPageRequestUsing(page, size);
        List<Booking> allBookings = new ArrayList<>();
        allBookings.add(createExpectedBooking());
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), allBookings.size());
        List<Booking> pageContent = allBookings.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, allBookings.size());
    }

    @Test
    void addBookingTest() {
        Booking expectedBooking = createExpectedBooking();
        User booker = expectedBooking.getBooker();
        Item item = expectedBooking.getItem();
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.findAvailableById(item.getId())).thenReturn(true);

        Booking actualBooking = bookingServiceImpl.addBooking(booker.getId(), expectedBooking);

        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    void addBookingWithNotAvailableItemTest() {
        Booking expectedBooking = createExpectedBooking();
        User booker = expectedBooking.getBooker();
        Item item = expectedBooking.getItem();
        expectedBooking.getItem().setAvailable(Boolean.FALSE);
        Mockito.when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        doThrow(NotFoundException.class).when(itemRepository).findAvailableById(item.getId());

        assertThrows(NotFoundException.class, () -> bookingServiceImpl.addBooking(booker.getId(), expectedBooking));
        verify(bookingRepository, never()).save(expectedBooking);
    }

    @Test
    void approvedTest() {
        Booking expectedBooking = createExpectedBooking();
        User booker = expectedBooking.getBooker();
        Item item = expectedBooking.getItem();
        Mockito.when(bookingRepository.findBookingById(expectedBooking.getId())).thenReturn(Optional.of(expectedBooking));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        Booking actualBooking = bookingServiceImpl.approved(item.getOwner(), expectedBooking.getId(), Boolean.TRUE);

        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    void approvedWithWrongBookingIdTest() {
        Booking expectedBooking = createExpectedBooking();
        Item item = expectedBooking.getItem();

        doThrow(NotFoundException.class).when(bookingRepository).findBookingById(expectedBooking.getId());

        assertThrows(NotFoundException.class, () -> bookingServiceImpl.approved(item.getOwner(), expectedBooking.getId(), Boolean.TRUE));
    }

    @Test
    void approvedWithWrongItemTest() {
        Booking expectedBooking = createExpectedBooking();
        User booker = expectedBooking.getBooker();
        Item item = expectedBooking.getItem();
        Mockito.when(bookingRepository.findBookingById(expectedBooking.getId())).thenReturn(Optional.of(expectedBooking));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));

        doThrow(NotFoundException.class).when(itemRepository).findById(any());

        assertThrows(NotFoundException.class, () -> bookingServiceImpl.approved(item.getOwner(), expectedBooking.getId(), Boolean.TRUE));
    }

    @Test
    void approvedWithApprovedStatusTest() {
        Booking expectedBooking = createExpectedBooking();
        expectedBooking.setStatus(BookingStatus.APPROVED);
        User booker = expectedBooking.getBooker();
        Item item = expectedBooking.getItem();
        Mockito.when(bookingRepository.findBookingById(expectedBooking.getId())).thenReturn(Optional.of(expectedBooking));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> bookingServiceImpl.approved(item.getOwner(), expectedBooking.getId(), Boolean.TRUE));
    }

    @Test
    void findBookingTest() {
        Booking expectedBooking = createExpectedBooking();
        User booker = expectedBooking.getBooker();
        Item item = expectedBooking.getItem();
        Mockito.when(bookingRepository.findBookingById(any())).thenReturn(Optional.of(expectedBooking));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        BookingDto actualBookingDto = bookingServiceImpl.findBooking(expectedBooking.getBookerId(), expectedBooking.getId());

        assertEquals(bookingMapper.toBookingDto(expectedBooking), actualBookingDto);
    }

    @Test
    void findAllBookerBookingsTest() {
        List<BookingDto> expectedListOfBookingsDto = new ArrayList<>();
        Booking expectedBooking = createExpectedBooking();
        User booker = expectedBooking.getBooker();
        Item item = expectedBooking.getItem();
        Page<Booking> bookingPage = getBookings(0, 1);
        expectedListOfBookingsDto.add(bookingMapper.toBookingDto(bookingPage.toList().get(0)));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findBookingByBookerIdPageable(PageRequest.of(0, 1), booker.getId())).thenReturn(bookingPage);

        List<BookingDto> actualBookingList = bookingServiceImpl.findAllBookerBookings(booker.getId(), null, 0, 1);

        assertEquals(expectedListOfBookingsDto, actualBookingList);
    }

    @Test
    void findAllOwnerBookingsTest() {
        List<BookingDto> expectedListOfBookingsDto = new ArrayList<>();
        Booking expectedBooking = createExpectedBooking();
        User booker = expectedBooking.getBooker();
        Item item = expectedBooking.getItem();
        Long ownerId = item.getOwner();
        Page<Booking> bookingPage = getBookings(0, 1);
        expectedListOfBookingsDto.add(bookingMapper.toBookingDto(bookingPage.toList().get(0)));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findBookingByOwnerIdPageable(PageRequest.of(0, 1), ownerId)).thenReturn(bookingPage);

        List<BookingDto> actualBookingList = bookingServiceImpl.findAllOwnerBookings(ownerId, null, 0, 1);

        assertEquals(expectedListOfBookingsDto, actualBookingList);
    }

    @Test
    void findBookingsByStatusTest() {
        List<BookingDto> expectedListOfBookingsDto = new ArrayList<>();
        Booking expectedBooking = createExpectedBooking();
        User booker = expectedBooking.getBooker();
        Item item = expectedBooking.getItem();

        List<Booking> actualBookingList = bookingServiceImpl.findBookingsByStatus(booker.getId(), String.valueOf(expectedBooking.getStatus()));
    }

    @Test
    void findOwnerBookingsByStatusTest() {
        List<BookingDto> expectedListOfBookingsDto = new ArrayList<>();
        Booking expectedBooking = createExpectedBooking();
        User booker = expectedBooking.getBooker();
        Item item = expectedBooking.getItem();

        List<Booking> actualBookingList = bookingServiceImpl.findOwnerBookingsByStatus(booker.getId(), String.valueOf(expectedBooking.getStatus()));
    }
}
