package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestBody @Valid BookingRequestDto bookingDto) {
        log.info("/bookings/ POST выполнение запроса. userId={}", userId);
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long bookingId,
                                     @RequestParam boolean approved) {
        log.info("/bookings/{bookingId} PATCH выполнение запроса. userId={}, bookingId = {}, approved = {}.", userId,
                bookingId, approved);
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long bookingId) {
        log.info("/bookings/{bookingId} GET выполнение запроса. userId={}, bookingId = {}.", userId, bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam(defaultValue = "ALL") String state) {
        log.info("/bookings/ GET выполнение запроса. userId={}, state = {}.", userId, state);
        return bookingService.getBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                             @RequestParam(defaultValue = "ALL") String state) {
        log.info("/bookings/ GET выполнение запроса. ownerId={}, state = {}.", ownerId, state);
        return bookingService.getOwnerBookings(ownerId, state);
    }

}
