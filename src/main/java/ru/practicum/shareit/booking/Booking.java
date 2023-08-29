package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Entity
@Table(name = "bookings", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private User booker;

    @Transient
    private Item item;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_booking")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date start;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_booking")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date end;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(name = "booker_id")
    private Long bookerId;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_Owner")
    private Long itemOwner;
}
