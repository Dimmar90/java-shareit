package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;

@Component
public class BookingMapper {

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getBooker(),
                booking.getItem(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }
}
