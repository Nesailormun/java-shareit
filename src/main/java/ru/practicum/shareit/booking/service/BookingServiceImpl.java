package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.RequestState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotItemOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto createBooking(long userId, BookingRequestDto bookingDto) {

        log.info("Создание нового бронирования пользователем с id={}", userId);
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + bookingDto.getItemId() + " не найдена"));

        if (bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new IllegalArgumentException("Время начала и конца бронирования не должны быть равны.");
        }
        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Вещь с id =" + item.getId() + " недоступна для бронирования.");
        }

        Booking booking = BookingMapper.mapToNewBooking(bookingDto, item, booker);

        return BookingMapper.mapToDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approveBooking(long userId, long bookingId, boolean approved) {
        log.info("Подтверждение бронирования с id={} пользователем с id={}, approved={}", bookingId, userId, approved);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));

        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotItemOwnerException("Пользователь не является владельцем вещи");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.mapToDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        log.info("Получение информации о бронировании с id={} пользователем с id={}", bookingId, userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));

        if (booking.getBooker().getId() != userId &&
                booking.getItem().getOwner().getId() != userId) {
            throw new NotItemOwnerException("Нет доступа к информации о бронировании.");
        }

        return BookingMapper.mapToDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByUser(long userId, String state) {
        log.info("Получение списка бронирований пользователя с id={}, state={}", userId, state);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookingList = switch (RequestState.from(state)) {
            case CURRENT -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
        };

        return bookingList.stream().map(BookingMapper::mapToDto).toList();
    }

    @Override
    public List<BookingDto> getOwnerBookings(long ownerId, String state) {
        log.info("Получение списка бронирований для владельца с id={}, state={}", ownerId, state);
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + ownerId + " не найден"));

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookingList = switch (RequestState.from(state)) {
            case CURRENT ->
                    bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
            case WAITING ->
                    bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
            default -> bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
        };

        return bookingList.stream().map(BookingMapper::mapToDto).toList();
    }
}
