package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    Booking addBooking(Long bookerId, Booking booking);

    Booking approved(Long sharerId, Long id, Boolean approved);

    BookingDto findBooking(Long userId, Long bookingId);

    List<BookingDto> findAllBookerBookings(Long bookerId, String bookingStatus, Integer from, Integer size);

    List<BookingDto> findAllOwnerBookings(Long ownerId, String bookingStatus, Integer from, Integer size);
}
