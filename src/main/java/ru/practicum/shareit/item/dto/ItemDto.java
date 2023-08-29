package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.Booking;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private Booking lastBooking;
    private Booking nextBooking;

    public ItemDto(Long id, String name, String description, Boolean available, Long requestId, Booking lastBooking, Booking nextBooking) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
    }
}
