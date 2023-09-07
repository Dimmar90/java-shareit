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
    @Query("UPDATE Booking b SET b.status = ?1 " +
            "WHERE b.id = ?2")
    void updateStatus(BookingStatus status, Long id);

    Optional<Booking> findBookingById(Long id);

    @Query("SELECT b FROM Booking AS b WHERE b.bookerId = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> findBookingByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.bookerId = ?1 AND b.status = 'WAITING' " +
            "ORDER BY b.start DESC")
    List<Booking> findWaitingBookingById(Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.bookerId = ?1 AND b.status = 'APPROVED' " +
            "ORDER BY b.start DESC")
    List<Booking> findApprovedBookingById(Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.bookerId = ?1 AND b.status = 'REJECTED' " +
            "ORDER BY b.start DESC")
    List<Booking> findRejectedBookingById(Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.bookerId = ?1 AND b.end < CURRENT_TIMESTAMP() " +
            "ORDER BY b.start DESC")
    List<Booking> findPastBookingById(Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.bookerId = ?1 AND b.start < CURRENT_TIMESTAMP() AND b.end > CURRENT_TIMESTAMP() " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentBookingById(Long bookerId);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.bookerId = ?1 AND b.start > CURRENT_TIMESTAMP() " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureBookingById(Long bookerId);

    @Query("SELECT b FROM Booking AS b JOIN Item AS i ON b.itemId = i.id " +
            "WHERE i.owner = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> findBookingByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking AS b JOIN Item AS i ON b.itemId = i.id " +
            "WHERE i.owner = ?1 AND b.status = 'WAITING' " +
            "ORDER BY b.start DESC")
    List<Booking> findWaitingBookingByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking AS b JOIN Item AS i ON b.itemId = i.id " +
            "WHERE i.owner = ?1 AND b.status = 'APPROVED' " +
            "ORDER BY b.start DESC")
    List<Booking> findApprovedBookingByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking AS b JOIN Item AS i ON b.itemId = i.id " +
            "WHERE i.owner = ?1 AND b.status = 'REJECTED' " +
            "ORDER BY b.start DESC")
    List<Booking> findRejectedBookingByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking AS b JOIN Item AS i ON b.itemId = i.id " +
            "WHERE i.owner = ?1 AND b.end < CURRENT_TIMESTAMP() " +
            "ORDER BY b.start DESC")
    List<Booking> findPastBookingByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking AS b JOIN Item AS i ON b.itemId = i.id " +
            "WHERE i.owner = ?1 AND b.start < CURRENT_TIMESTAMP() AND b.end > CURRENT_TIMESTAMP() " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentBookingByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking AS b JOIN Item AS i ON b.itemId = i.id " +
            "WHERE i.owner = ?1 AND b.start > CURRENT_TIMESTAMP() " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureBookingByOwnerId(Long bookerId);

    @Query("SELECT b FROM Booking AS b JOIN Item AS i ON b.itemId = i.id " +
            "WHERE i.id = ?1 AND b.itemOwner = ?2 AND b.start < CURRENT_TIMESTAMP()" +
            "ORDER BY b.start DESC")
    List<Booking> findLastItemBooking(Long itemId, Long itemOwner);

    @Query("SELECT b FROM Booking AS b JOIN Item AS i ON b.itemId = i.id " +
            "WHERE i.id = ?1 AND b.itemOwner = ?2 AND b.start > CURRENT_TIMESTAMP() " +
            "ORDER BY b.start")
    List<Booking> findNextItemBooking(Long itemId, Long itemOwner);

    @Query("SELECT COUNT(b.bookerId) FROM Booking AS b JOIN User AS u ON b.bookerId = u.id " +
            "WHERE u.id = ?1 AND b.itemId = ?2 AND b.start < CURRENT_TIMESTAMP()")
    int countUserBookingsOfItem(Long userId, Long itemId);
}