package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Modifying
    @Query("update Booking b set b.status = ?1 where b.id = ?2")
    void updateStatus(BookingStatus status, Long id);

    Optional<Booking> findBookingById(Long id);

    @Query("select b from Booking as b where b.bookerId = ?1 order by b.start desc")
    List<Booking> findBookingByBookerId(Long bookerId);

    @Query("select b from Booking as b where b.bookerId = ?1 and b.status = 'WAITING' order by b.start desc")
    List<Booking> findWaitingBookingById(Long bookerId);

    @Query("select b from Booking as b where b.bookerId = ?1 and b.status = 'APPROVED' order by b.start desc")
    List<Booking> findApprovedBookingById(Long bookerId);

    @Query("select b from Booking as b where b.bookerId = ?1 and b.status = 'REJECTED' order by b.start desc")
    List<Booking> findRejectedBookingById(Long bookerId);

    @Query("select b from Booking as b where b.bookerId = ?1 and b.end < CURRENT_TIMESTAMP()  order by b.start desc")
    List<Booking> findPastBookingById(Long bookerId);

    @Query("select b from Booking as b where b.bookerId = ?1 and b.start < CURRENT_TIMESTAMP() and b.end > CURRENT_TIMESTAMP() order by b.start desc")
    List<Booking> findCurrentBookingById(Long bookerId);

    @Query("select b from Booking as b where b.bookerId = ?1 and b.start > CURRENT_TIMESTAMP() order by b.start desc")
    List<Booking> findFutureBookingById(Long bookerId);

    @Query("select b from Booking as b join Item as i on b.itemId = i.id where i.owner = ?1 order by b.start desc")
    List<Booking> findBookingByOwnerId(Long ownerId);

    @Query("select b from Booking as b join Item as i on b.itemId = i.id where i.owner = ?1 and b.status = 'WAITING' order by b.start desc")
    List<Booking> findWaitingBookingByOwnerId(Long ownerId);

    @Query("select b from Booking as b join Item as i on b.itemId = i.id where i.owner = ?1 and b.status = 'APPROVED' order by b.start desc")
    List<Booking> findApprovedBookingByOwnerId(Long ownerId);

    @Query("select b from Booking as b join Item as i on b.itemId = i.id where i.owner = ?1 and b.status = 'REJECTED' order by b.start desc")
    List<Booking> findRejectedBookingByOwnerId(Long ownerId);

    @Query("select b from Booking as b join Item as i on b.itemId = i.id where i.owner = ?1 and  b.end < CURRENT_TIMESTAMP() order by b.start desc")
    List<Booking> findPastBookingByOwnerId(Long ownerId);

    @Query("select b from Booking as b join Item as i on b.itemId = i.id where i.owner = ?1 and  b.start < CURRENT_TIMESTAMP() and b.end > CURRENT_TIMESTAMP() order by b.start desc")
    List<Booking> findCurrentBookingByOwnerId(Long ownerId);

    @Query("select b from Booking as b join Item as i on b.itemId = i.id where i.owner = ?1 and  b.start > CURRENT_TIMESTAMP() order by b.start desc")
    List<Booking> findFutureBookingByOwnerId(Long bookerId);

    @Query("select b from Booking as b join Item as i on b.itemId = i.id where i.id = ?1 and b.itemOwner = ?2 and b.end < CURRENT_TIMESTAMP() order by b.end")
    List<Booking> findLastBooking(Long itemId, Long itemOwner);
}
