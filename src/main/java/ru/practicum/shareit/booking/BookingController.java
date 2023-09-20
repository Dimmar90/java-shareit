package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                 @RequestBody Booking booking) {
        return new ResponseEntity<>(bookingService.addBooking(bookerId, booking), CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> approve(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                     @PathVariable("id") Long id,
                                     @RequestParam(value = "approved") Boolean approved) {
        return new ResponseEntity<>(bookingService.approved(bookerId, id, approved), OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable("id") Long id) {
        return new ResponseEntity<>(bookingService.findBooking(userId, id), OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                @RequestParam(value = "state", required = false) String bookingState,
                                                @RequestParam(value = "from", required = false) @Min(0) Integer from,
                                                @RequestParam(value = "size", required = false) @Min(1) @Max(100) Integer size) {
        return new ResponseEntity<>(bookingService.findAllBookerBookings(bookerId, bookingState, from, size), OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<?> getAllOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                 @RequestParam(value = "state", required = false) String bookingState,
                                                 @RequestParam(value = "from", required = false) Integer from,
                                                 @RequestParam(value = "size", required = false) Integer size) {
        return new ResponseEntity<>(bookingService.findAllOwnerBookings(ownerId, bookingState, from, size), OK);
    }
}
