package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;

import java.util.List;

public interface BookingService {
    Booking addBooking(Long bookerId, Booking booking);

    Booking approved(Long sharerId, Long id, Boolean approved);

    Booking findBooking(Long userId, Long bookingId);

    List<Booking> findAllBookerBookings(Long bookerId, String bookingStatus);

    List<Booking> findAllOwnerBookings(Long ownerId, String bookingStatus);
}
