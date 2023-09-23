package ru.practicum.shareit;

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
import static org.mockito.Mockito.any;

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

    public Item createItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("itemName");
        item.setDescription("itemDescription");
        item.setAvailable(true);
        item.setOwner(2L);
        return item;
    }

    private Booking createBooking() {
        Booking expectedBooking = new Booking();
        expectedBooking.setId(1L);
        expectedBooking.setItemId(1L);
        expectedBooking.setStart(LocalDateTime.of(2023, 10, 20, 10, 00));
        expectedBooking.setEnd(LocalDateTime.of(2023, 10, 21, 10, 00));
        expectedBooking.setBooker(createBooker());
        expectedBooking.setItem(createItem());
        expectedBooking.setStatus(BookingStatus.WAITING);
        return expectedBooking;
    }

    private List<Booking> createBookingList(Booking booking) {
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        return bookingList;
    }

    private List<BookingDto> convertBookingListToBookingsDtoList(List<Booking> bookingList) {
        List<BookingDto> listOfBookingsDto = new ArrayList<>();
        for (Booking booking : bookingList) {
            listOfBookingsDto.add(bookingMapper.toBookingDto(booking));
        }
        return listOfBookingsDto;
    }

    private Pageable createPageRequestUsing(int page, int size) {
        return PageRequest.of(page, size);
    }

    private Page<Booking> getBookings(int page, int size) {
        Pageable pageRequest = createPageRequestUsing(page, size);
        List<Booking> allBookings = new ArrayList<>();
        allBookings.add(createBooking());
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), allBookings.size());
        List<Booking> pageContent = allBookings.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, allBookings.size());
    }

    @Test
    void addBookingTest() {
        Booking expectedBooking = createBooking();
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
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.findAvailableById(item.getId())).thenReturn(false);

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingServiceImpl.addBooking(booker.getId(), booking)
        );

        assertEquals("Item not available for booking", exception.getMessage());
    }

    @Test
    void addBookingWithWrongBookerIdTest() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        booking.getItem().setOwner(1L);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.findAvailableById(item.getId())).thenReturn(true);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingServiceImpl.addBooking(booker.getId(), booking)
        );

        assertEquals("Item belong to owner", exception.getMessage());
    }

    @Test
    void approvedTest() {
        Booking expectedBooking = createBooking();
        User booker = expectedBooking.getBooker();
        Item item = expectedBooking.getItem();
        Mockito.when(bookingRepository.findBookingById(expectedBooking.getId())).thenReturn(Optional.of(expectedBooking));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        Booking actualBooking = bookingServiceImpl.approved(item.getOwner(), expectedBooking.getId(), Boolean.TRUE);

        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    void approvedWithWrongOwnerId() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        Long wrongOwnerId = 3L;
        Mockito.when(bookingRepository.findBookingById(booking.getId())).thenReturn(Optional.of(booking));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingServiceImpl.approved(wrongOwnerId, booking.getId(), Boolean.TRUE)
        );

        assertEquals("Set wrong id for booking approved, " + wrongOwnerId + " is not owner id for item id : " + booking.getItem().getId(),
                exception.getMessage());
    }

    @Test
    void approvedWithApprovedStatus() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        booking.setStatus(BookingStatus.APPROVED);
        Mockito.when(bookingRepository.findBookingById(booking.getId())).thenReturn(Optional.of(booking));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingServiceImpl.approved(item.getOwner(), booking.getId(), Boolean.TRUE)
        );

        assertEquals("Already Approved", exception.getMessage());
    }

    @Test
    void approvedWithFalseApproved() {
        Booking expectedBooking = createBooking();
        User booker = expectedBooking.getBooker();
        Item item = expectedBooking.getItem();
        Mockito.when(bookingRepository.findBookingById(expectedBooking.getId())).thenReturn(Optional.of(expectedBooking));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        Booking actualBooking = bookingServiceImpl.approved(item.getOwner(), expectedBooking.getId(), Boolean.FALSE);

        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    void findBookingTest() {
        Booking expectedBooking = createBooking();
        User booker = expectedBooking.getBooker();
        Item item = expectedBooking.getItem();
        Mockito.when(bookingRepository.findBookingById(any())).thenReturn(Optional.of(expectedBooking));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        BookingDto actualBookingDto = bookingServiceImpl.findBooking(expectedBooking.getBookerId(), expectedBooking.getId());

        assertEquals(bookingMapper.toBookingDto(expectedBooking), actualBookingDto);
    }

    @Test
    void findBookingWithWrongUserId() {
        Booking expectedBooking = createBooking();
        User booker = expectedBooking.getBooker();
        Item item = expectedBooking.getItem();
        Long wrongUserId = 3L;
        Mockito.when(bookingRepository.findBookingById(any())).thenReturn(Optional.of(expectedBooking));
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingServiceImpl.findBooking(wrongUserId, expectedBooking.getId())
        );

        assertEquals("User can't find item booking", exception.getMessage());
    }

    @Test
    void findAllBookerBookingsTest() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        List<Booking> bookingList = createBookingList(booking);
        List<BookingDto> expectedListOfBookingsDto = convertBookingListToBookingsDtoList(bookingList);
        Page<Booking> bookingPage = getBookings(0, 1);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findBookingByBookerIdPageable(PageRequest.of(0, 1), booker.getId())).thenReturn(bookingPage);

        List<BookingDto> actualBookingList = bookingServiceImpl.findAllBookerBookings(booker.getId(), null, 0, 1);

        assertEquals(expectedListOfBookingsDto, actualBookingList);
    }

    @Test
    void findAllBookerBookingsWithWrongPageableSettingsTest() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        int wrongPageableSetting = -1;
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingServiceImpl.findAllBookerBookings(booker.getId(), null, 0, wrongPageableSetting)
        );

        assertEquals("Wrong pageable settings : size is 0 , can't be < 1", exception.getMessage());
    }

    @Test
    void findAllBookerBookingsByBookerIdTest() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        List<Booking> bookingList = createBookingList(booking);

        List<BookingDto> expectedListOfBookingsDto = convertBookingListToBookingsDtoList(bookingList);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findBookingByBookerId(booker.getId())).thenReturn(bookingList);

        List<BookingDto> actualBookingList = bookingServiceImpl.findAllBookerBookings(booker.getId(), null, null, null);

        assertEquals(expectedListOfBookingsDto, actualBookingList);
    }

    @Test
    void findAllBookerBookingsByStatusTest() {
        Booking booking = createBooking();
        booking.setStatus(BookingStatus.FUTURE);
        User booker = booking.getBooker();
        Item item = booking.getItem();
        List<Booking> bookingList = createBookingList(booking);
        List<BookingDto> expectedListOfBookingsDto = convertBookingListToBookingsDtoList(bookingList);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findFutureBookingById(booker.getId())).thenReturn(bookingList);

        List<BookingDto> actualBookingList = bookingServiceImpl.findAllBookerBookings(booker.getId(), "FUTURE", null, null);

        assertEquals(expectedListOfBookingsDto, actualBookingList);
    }

    @Test
    void findAllBookerBookingsByUnknownStatusTest() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingServiceImpl.findAllBookerBookings(booker.getId(), "Unknown Status", null, null)
        );

        assertEquals("Unknown state: Unknown Status", exception.getMessage());
    }

    @Test
    void findAllOwnerBookingsTest() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        Long ownerId = item.getOwner();
        List<Booking> bookingList = createBookingList(booking);
        List<BookingDto> expectedListOfBookingsDto = convertBookingListToBookingsDtoList(bookingList);
        Page<Booking> bookingPage = getBookings(0, 1);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findBookingByOwnerIdPageable(PageRequest.of(0, 1), ownerId)).thenReturn(bookingPage);

        List<BookingDto> actualBookingList = bookingServiceImpl.findAllOwnerBookings(ownerId, null, 0, 1);

        assertEquals(expectedListOfBookingsDto, actualBookingList);
    }

    @Test
    void findAllOwnerBookingsWithWrongPageableSettingsTest() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        Long ownerId = item.getOwner();
        int wrongPageableSetting = -1;
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingServiceImpl.findAllOwnerBookings(ownerId, null, 0, wrongPageableSetting)
        );

        assertEquals("Wrong pageable settings : size is 0 , can't be < 1", exception.getMessage());
    }

    @Test
    void findAllOwnerBookingsByBookerIdTest() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        Long ownerId = item.getOwner();
        List<Booking> bookingList = createBookingList(booking);
        List<BookingDto> expectedListOfBookingsDto = convertBookingListToBookingsDtoList(bookingList);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findBookingByOwnerId(ownerId)).thenReturn(bookingList);

        List<BookingDto> actualBookingList = bookingServiceImpl.findAllOwnerBookings(ownerId, null, null, null);

        assertEquals(expectedListOfBookingsDto, actualBookingList);
    }

    @Test
    void findAllOwnerBookingsByStatusTest() {
        Booking booking = createBooking();
        booking.setStatus(BookingStatus.FUTURE);
        User booker = booking.getBooker();
        Item item = booking.getItem();
        Long ownerId = item.getOwner();
        List<Booking> bookingList = createBookingList(booking);
        List<BookingDto> expectedListOfBookingsDto = convertBookingListToBookingsDtoList(bookingList);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findFutureBookingByOwnerId(ownerId)).thenReturn(bookingList);

        List<BookingDto> actualBookingList = bookingServiceImpl.findAllOwnerBookings(ownerId, "FUTURE", null, null);

        assertEquals(expectedListOfBookingsDto, actualBookingList);
    }

    @Test
    void findAllOwnerBookingsByUnknownStatusTest() {
        Booking booking = createBooking();
        User booker = booking.getBooker();
        Item item = booking.getItem();
        Long ownerId = item.getOwner();
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingServiceImpl.findAllOwnerBookings(ownerId, "Unknown Status", null, null)
        );

        assertEquals("Unknown state: Unknown Status", exception.getMessage());
    }

    @Test
    void validateWithEmptyBookingDates() {
        Booking booking = createBooking();
        booking.setStart(null);
        User booker = booking.getBooker();
        Item item = booking.getItem();
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingServiceImpl.validateBooking(booker.getId(), booking)
        );

        assertEquals("Empty booking dates", exception.getMessage());
    }

    @Test
    void validateWithWrongBookingDates() {
        Booking booking = createBooking();
        booking.setStart(LocalDateTime.now().minusMinutes(10));
        User booker = booking.getBooker();
        Item item = booking.getItem();
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.findAvailableById(booking.getId())).thenReturn(true);

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> bookingServiceImpl.validateBooking(booker.getId(), booking)
        );

        assertEquals("Not correct booking dates", exception.getMessage());
    }
}
