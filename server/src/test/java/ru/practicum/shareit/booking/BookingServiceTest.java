package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotItemOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class BookingServiceTest {

    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

    @Autowired
    BookingService bookingService;

    private UserDto user1;
    private UserDto user2;
    private BookingRequestDto bookingRequestDto;
    private ItemDto itemDto;

    @BeforeEach
    void initTest() {
        user1 = new UserDto();
        user1.setName("user1");
        user1.setEmail("user1@example.com");

        user2 = new UserDto();
        user2.setName("user2");
        user2.setEmail("user2@example.com");

        itemDto = new ItemDto();
        itemDto.setName("item1");
        itemDto.setDescription("Description for item1");
        itemDto.setAvailable(true);
    }

    private BookingPreparation prepareBookingDataTest() {
        UserDto createdUser1 = userService.createUser(user1);
        UserDto createdUser2 = userService.createUser(user2);

        ItemDto createdItem = itemService.addNewItem(createdUser1.getId(), itemDto);

        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(createdItem.getId());
        requestDto.setStart(LocalDateTime.now().plusHours(1));
        requestDto.setEnd(LocalDateTime.now().plusHours(2));

        return new BookingPreparation(createdUser1, createdUser2, createdItem, requestDto);
    }

    private record BookingPreparation(UserDto owner, UserDto booker, ItemDto item,
                                      BookingRequestDto bookingRequestDto) {
    }

    @Test
    void createBookingSuccessTest() {
        BookingPreparation prep = prepareBookingDataTest();

        BookingDto bookingDto = bookingService.createBooking(prep.booker.getId(), prep.bookingRequestDto);

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getItem().getId()).isEqualTo(prep.bookingRequestDto.getItemId());
        assertThat(bookingDto.getBooker().getId()).isEqualTo(prep.booker.getId());
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.WAITING.toString());
        assertThat(bookingDto.getStart()).isEqualTo(prep.bookingRequestDto.getStart());
        assertThat(bookingDto.getEnd()).isEqualTo(prep.bookingRequestDto.getEnd());
    }

    @Test
    void createBookingWithInvalidDatesThrowsTest() {
        BookingPreparation prep = prepareBookingDataTest();

        prep.bookingRequestDto.setStart(LocalDateTime.now().plusHours(2));
        prep.bookingRequestDto.setEnd(LocalDateTime.now().plusHours(1)); // end before start

        assertThatThrownBy(() -> bookingService.createBooking(prep.booker.getId(), prep.bookingRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Некорректные дата начала и окончания бронирования");
    }

    @Test
    void createBookingUnavailableItemThrowsTest() {
        BookingPreparation prep = prepareBookingDataTest();

        ItemDto unavailableItem = new ItemDto();
        unavailableItem.setName("unavailableItem");
        unavailableItem.setDescription("Unavailable");
        unavailableItem.setAvailable(false);

        ItemDto createdUnavailableItem = itemService.addNewItem(prep.owner.getId(), unavailableItem);

        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(createdUnavailableItem.getId());
        requestDto.setStart(LocalDateTime.now().plusHours(1));
        requestDto.setEnd(LocalDateTime.now().plusHours(2));

        assertThatThrownBy(() -> bookingService.createBooking(prep.booker.getId(), requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("недоступна для бронирования");
    }

    @Test
    void approveBookingSuccessTest() {
        BookingPreparation prep = prepareBookingDataTest();

        BookingDto createdBooking = bookingService.createBooking(prep.booker.getId(), prep.bookingRequestDto);

        BookingDto approvedBooking = bookingService.approveBooking(prep.owner.getId(), createdBooking.getId(), true);

        assertThat(approvedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED.toString());
        assertThat(approvedBooking.getId()).isEqualTo(createdBooking.getId());
    }

    @Test
    void approveBookingByNonOwnerThrowsTest() {
        BookingPreparation prep = prepareBookingDataTest();

        BookingDto createdBooking = bookingService.createBooking(prep.booker.getId(), prep.bookingRequestDto);

        assertThatThrownBy(() -> bookingService.approveBooking(prep.booker.getId(), createdBooking.getId(), true))
                .isInstanceOf(NotItemOwnerException.class)
                .hasMessageContaining("Пользователь не является владельцем вещи");
    }

    @Test
    void getBookingByIdAsOwnerOrBookerSuccessTest() {
        BookingPreparation prep = prepareBookingDataTest();

        BookingDto createdBooking = bookingService.createBooking(prep.booker.getId(), prep.bookingRequestDto);

        BookingDto byBooker = bookingService.getBookingById(prep.booker.getId(), createdBooking.getId());
        BookingDto byOwner = bookingService.getBookingById(prep.owner.getId(), createdBooking.getId());

        assertThat(byBooker.getId()).isEqualTo(createdBooking.getId());
        assertThat(byOwner.getId()).isEqualTo(createdBooking.getId());
    }

    @Test
    void getBookingByIdNoAccessThrowsTest() {
        BookingPreparation prep = prepareBookingDataTest();

        UserDto user3 = new UserDto();
        user3.setName("user3");
        user3.setEmail("user3@example.com");
        UserDto createdUser3 = userService.createUser(user3);

        BookingDto createdBooking = bookingService.createBooking(prep.booker.getId(), prep.bookingRequestDto);

        assertThatThrownBy(() -> bookingService.getBookingById(createdUser3.getId(), createdBooking.getId()))
                .isInstanceOf(NotItemOwnerException.class)
                .hasMessageContaining("Нет доступа к информации");
    }

    @Test
    void getBookingsByUserTest() {
        BookingPreparation prep = prepareBookingDataTest();

        BookingDto createdBooking = bookingService.createBooking(prep.booker.getId(), prep.bookingRequestDto);

        List<BookingDto> bookings = bookingService.getBookingsByUser(prep.booker.getId(), "ALL");

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.getFirst().getId()).isEqualTo(createdBooking.getId());
    }

    @Test
    void getOwnerBookingsTest() {
        BookingPreparation prep = prepareBookingDataTest();

        BookingDto createdBooking = bookingService.createBooking(prep.booker.getId(), prep.bookingRequestDto);

        List<BookingDto> bookings = bookingService.getOwnerBookings(prep.owner.getId(), "ALL");

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.getFirst().getId()).isEqualTo(createdBooking.getId());
    }
}
