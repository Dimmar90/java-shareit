package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class BookingDto {
    private Long id;
    private User booker;
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private Long bookerId;
    private Long itemId;
    private String itemName;
    private Long itemOwner;


    public BookingDto(Long id, User booker, Item item, LocalDateTime start, LocalDateTime end,
                      BookingStatus status, Long bookerId, Long itemId, String itemName, Long itemOwner) {
        this.id = id;
        this.booker = booker;
        this.item = item;
        this.start = start;
        this.end = end;
        this.status = status;
        this.bookerId = bookerId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemOwner = itemOwner;
    }
}
