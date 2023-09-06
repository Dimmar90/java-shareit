package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    private final BookingMapper mapper;

    public BookingServiceImpl(UserRepository userRepository, ItemRepository itemRepository,
                              BookingRepository bookingRepository, BookingMapper mapper) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.mapper = mapper;
    }

    public Booking addBooking(Long bookerId, Booking booking) {
        validateBooking(bookerId, booking);
        setItemAndBookerToBooking(booking, bookerId);
        if (!booking.getItem().getAvailable()) {
            throw new NotFoundException("Вещь не доступна для бронирования");
        }
        if (bookerId.equals(booking.getItem().getOwner())) {
            throw new NotFoundException("Вещь принадлежит пользователю");
        }
        booking.setStatus(BookingStatus.WAITING);
        booking.setBookerId(bookerId);
        booking.setItemName(booking.getItem().getName());
        booking.setItemOwner(booking.getItem().getOwner());
        bookingRepository.save(booking);
        return booking;
    }

    public Booking approved(Long sharerId, Long id, Boolean approved) {
        Booking booking = bookingRepository.findBookingById(id).orElseThrow(() -> new NotFoundException("Не найдено бронирование id: " + id));
        setItemAndBookerToBooking(booking, booking.getBookerId());
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BadRequestException("Already Approved");
        }
        if (!Objects.equals(sharerId, booking.getItem().getOwner())) {
            String message = "Не верно указан id владельца";
            log.warn(message);
            throw new NotFoundException(message);
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            bookingRepository.updateStatus(BookingStatus.APPROVED, id);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            bookingRepository.updateStatus(BookingStatus.REJECTED, id);
        }
        return booking;
    }

    public BookingDto findBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findBookingById(bookingId).orElseThrow(() -> new NotFoundException("Не найдено бронирование id: " + bookingId));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найден пользователь id: " + userId));
        setItemAndBookerToBooking(booking, booking.getBookerId());
        Long ownerId = booking.getItem().getOwner();
        if (Objects.equals(userId, booking.getBookerId()) || Objects.equals(userId, ownerId)) {
            return mapper.toBookingDto(booking);
        } else {
            String message = "Нет доступа";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    public List<BookingDto> findAllBookerBookings(Long bookerId, String bookingState) {
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException("Не найден пользователь id: " + bookerId));
        List<Booking> bookerBookings = new ArrayList<>();
        List<BookingDto> bookerBookingsDto = new ArrayList<>();

        if (bookingState == null) {
            bookerBookings = bookingRepository.findBookingByBookerId(bookerId);
        } else {
            bookerBookingsDto = findBookingsByStatus(bookerId, bookingState);
        }

        for (int bookerBookingsIndex = 0; bookerBookingsIndex < bookerBookings.size(); bookerBookingsIndex++) {
            setItemAndBookerToBooking(bookerBookings.get(bookerBookingsIndex), bookerId);
            bookerBookingsDto.add(mapper.toBookingDto(bookerBookings.get(bookerBookingsIndex)));
        }
        return bookerBookingsDto;
    }

    public List<BookingDto> findBookingsByStatus(Long bookerId, String bookingStatus) {
        List<Booking> bookerBookings = new ArrayList<>();
        List<BookingDto> bookerBookingsDto = new ArrayList<>();
        boolean isStatusSupported = false;

        for (BookingStatus state : BookingStatus.values()) {
            if (state.name().equals(bookingStatus)) {
                isStatusSupported = true;
            }
        }

        if (isStatusSupported) {
            switch (bookingStatus) {
                case "ALL":
                    bookerBookings = bookingRepository.findBookingByBookerId(bookerId);
                    break;
                case "WAITING":
                    bookerBookings = bookingRepository.findWaitingBookingById(bookerId);
                    break;
                case "APPROVED":
                    bookerBookings = bookingRepository.findApprovedBookingById(bookerId);
                    break;
                case "REJECTED":
                    bookerBookings = bookingRepository.findRejectedBookingById(bookerId);
                    break;
                case "PAST":
                    bookerBookings = bookingRepository.findPastBookingById(bookerId);
                    break;
                case "CURRENT":
                    bookerBookings = bookingRepository.findCurrentBookingById(bookerId);
                    break;
                case "FUTURE":
                    bookerBookings = bookingRepository.findFutureBookingById(bookerId);
                    break;
            }
        } else {
            String message = "Unknown state: " + bookingStatus;
            log.warn(message);
            throw new BadRequestException(message);
        }

        for (Booking booking : bookerBookings) {
            bookerBookingsDto.add(mapper.toBookingDto(booking));
        }
        return bookerBookingsDto;
    }


    public List<BookingDto> findAllOwnerBookings(Long ownerId, String bookingState) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Не найден пользователь id: " + ownerId));
        List<Booking> bookerBookings = new ArrayList<>();
        List<BookingDto> bookerBookingsDto = new ArrayList<>();

        if (bookingState == null) {
            bookerBookings = bookingRepository.findBookingByOwnerId(ownerId);
        } else {
            bookerBookingsDto = findOwnerBookingsByStatus(ownerId, bookingState);
        }

        for (int bookerBookingsIndex = 0; bookerBookingsIndex < bookerBookings.size(); bookerBookingsIndex++) {
            Long bookerId = bookerBookings.get(bookerBookingsIndex).getBookerId();
            setItemAndBookerToBooking(bookerBookings.get(bookerBookingsIndex), bookerId);
            bookerBookingsDto.add(mapper.toBookingDto(bookerBookings.get(bookerBookingsIndex)));
        }
        return bookerBookingsDto;
    }

    public List<BookingDto> findOwnerBookingsByStatus(Long ownerId, String bookingStatus) {
        List<Booking> bookerBookings = new ArrayList<>();
        List<BookingDto> bookerBookingsDto = new ArrayList<>();
        boolean isStatusSupported = false;

        for (BookingStatus state : BookingStatus.values()) {
            if (state.name().equals(bookingStatus)) {
                isStatusSupported = true;
            }
        }

        if (isStatusSupported) {
            switch (bookingStatus) {
                case "ALL":
                    bookerBookings = bookingRepository.findBookingByOwnerId(ownerId);
                    break;
                case "WAITING":
                    bookerBookings = bookingRepository.findWaitingBookingByOwnerId(ownerId);
                    break;
                case "APPROVED":
                    bookerBookings = bookingRepository.findApprovedBookingByOwnerId(ownerId);
                    break;
                case "REJECTED":
                    bookerBookings = bookingRepository.findRejectedBookingByOwnerId(ownerId);
                    break;
                case "PAST":
                    bookerBookings = bookingRepository.findPastBookingByOwnerId(ownerId);
                    break;
                case "CURRENT":
                    bookerBookings = bookingRepository.findCurrentBookingByOwnerId(ownerId);
                    break;
                case "FUTURE":
                    bookerBookings = bookingRepository.findFutureBookingByOwnerId(ownerId);
                    break;
            }
        } else {
            String message = "Unknown state: " + bookingStatus;
            log.warn(message);
            throw new BadRequestException(message);
        }

        for (Booking booking : bookerBookings) {
            bookerBookingsDto.add(mapper.toBookingDto(booking));
        }
        return bookerBookingsDto;
    }

    public void validateBooking(Long bookerId, Booking booking) {
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException("Не найден пользователь id: " + bookerId));
        Item item = itemRepository.findById(booking.getItemId()).orElseThrow(() -> new NotFoundException("Не найдена вещь id: " + booking.getItemId()));
        if (booking.getStart() == null || booking.getEnd() == null) {
            String message = "Не указаны даты бронирования";
            log.warn(message);
            throw new BadRequestException(message);
        }
        if (!itemRepository.findAvailableById(booking.getItemId())) {
            String message = "Вещь не доступна";
            log.warn(message);
            throw new BadRequestException(message);
        }
        LocalDateTime start = booking.getStart().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime end = booking.getEnd().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        if (start.isBefore(LocalDateTime.now()) || end.isBefore(start) || start.equals(end)) {
            String message = "Не верно указаны даты бронирования";
            log.warn(message);
            throw new BadRequestException(message);
        }
    }

    public void setItemAndBookerToBooking(Booking booking, Long bookerId) {
        User booker = userRepository.findById(bookerId).get();
        Item item = itemRepository.findById(booking.getItemId()).get();
        booking.setBooker(booker);
        booking.setItem(item);
    }
}
