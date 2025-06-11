package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotItemOwnerException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.exception.CommentBeforeBookingEndException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setup() {
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        owner = new User();
        owner.setName("Владимир");
        owner.setEmail("vladimir@example.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Алексей");
        booker.setEmail("alexey@example.com");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Дрель");
        item.setDescription("Электро-дрель");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
    }

    @Test
    void addNewItemTest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Лопата");
        itemDto.setDescription("Металлическая");
        itemDto.setAvailable(true);

        ItemDto created = itemService.addNewItem(owner.getId(), itemDto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Лопата");
        assertThat(created.getDescription()).isEqualTo("Металлическая");
        assertThat(created.getAvailable()).isTrue();
        assertThat(created.getOwnerId()).isEqualTo(owner.getId());
    }

    @Test
    void updateItemTest() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Перфоратор");
        updateDto.setDescription("С мощным мотором");
        updateDto.setAvailable(false);

        ItemDto updated = itemService.updateItem(owner.getId(), item.getId(), updateDto);

        assertThat(updated.getId()).isEqualTo(item.getId());
        assertThat(updated.getName()).isEqualTo("Перфоратор");
        assertThat(updated.getDescription()).isEqualTo("С мощным мотором");
        assertThat(updated.getAvailable()).isFalse();
    }

    @Test
    void updateItemNotOwnerThrows() {
        User otherUser = new User();
        otherUser.setName("Другой");
        otherUser.setEmail("other@example.com");
        otherUser = userRepository.save(otherUser);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Не должно обновиться");

        User finalOtherUser = otherUser;

        assertThrows(NotItemOwnerException.class, () -> {
            itemService.updateItem(finalOtherUser.getId(), item.getId(), updateDto);
        });
    }

    @Test
    void getItemByIdTest() {
        ItemWithBookingsDto dto = itemService.getItemById(owner.getId(), item.getId());

        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getName()).isEqualTo(item.getName());
        assertThat(dto.getDescription()).isEqualTo(item.getDescription());
        assertThat(dto.getAvailable()).isEqualTo(item.getAvailable());
        assertThat(dto.getOwnerId()).isEqualTo(owner.getId());
    }

    @Test
    void getUsersItemsTest() {
        List<ItemWithBookingsDto> items = itemService.getUsersItems(owner.getId());

        assertThat(items).isNotEmpty();
        assertThat(items.getFirst().getId()).isEqualTo(item.getId());
    }

    @Test
    void getItemsByTextTest() {
        List<ItemDto> found = itemService.getItemsByText(owner.getId(), "дрель");

        assertThat(found).isNotEmpty();
        assertThat(found.getFirst().getName().toLowerCase()).contains("дрель");
    }

    @Test
    void deleteItemTest() {
        itemService.deleteItem(owner.getId(), item.getId());
        assertThat(itemRepository.findById(item.getId())).isEmpty();
    }

    @Test
    void deleteItemNotOwnerThrows() {
        User otherUser = new User();
        otherUser.setName("Другой");
        otherUser.setEmail("other@example.com");
        otherUser = userRepository.save(otherUser);

        User finalOtherUser = otherUser;
        assertThrows(NotItemOwnerException.class, () -> {
            itemService.deleteItem(finalOtherUser.getId(), item.getId());
        });
    }

    @Test
    void addCommentTest() {

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().minusDays(3));
        booking.setEnd(LocalDateTime.now().minusDays(1));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Отличный инструмент!");

        CommentDto savedComment = itemService.addComment(booker.getId(), item.getId(), commentDto);

        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getText()).isEqualTo("Отличный инструмент!");
        assertThat(savedComment.getAuthorName()).isEqualTo(booker.getName());
    }

    @Test
    void addCommentWithoutPastBookingThrows() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Нельзя комментировать без бронирования");

        assertThrows(CommentBeforeBookingEndException.class, () -> {
            itemService.addComment(booker.getId(), item.getId(), commentDto);
        });
    }

    @Test
    void addCommentNotFoundUserThrows() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Текст");

        assertThrows(NotFoundException.class, () -> {
            itemService.addComment(9999L, item.getId(), commentDto);
        });
    }

    @Test
    void addCommentNotFoundItemThrows() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Текст");

        assertThrows(NotFoundException.class, () -> {
            itemService.addComment(booker.getId(), 9999L, commentDto);
        });
    }

}
