package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotItemOwnerException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.exception.CommentBeforeBookingEndException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        log.info("Обработка запроса на добавление нового предмета пользователем с id = {}", userId);
        User owner = checkUserExisting(userId);

        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос на вещь с id = " + itemDto.getRequestId() + " не найден."));
        }

        Item item = ItemMapper.mapToNewItem(itemDto, owner, request);
        item = itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        log.info("Запрос на обновление вещи с id = {} пользователем с id = {}", itemId, userId);
        Item item = checkItemExisting(itemId);
        checkItemOwner(userId, item);

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        item = itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemWithBookingsDto getItemById(long userId, long itemId) {
        log.info("Запрос на получение вещи с id = {} пользователем с id = {}", itemId, userId);

        Item item = checkItemExisting(itemId);

        ItemWithBookingsDto itemWithBookingsDto = ItemMapper.mapToItemWithBookingsDto(item, null, null);

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();
        itemWithBookingsDto.setComments(comments);

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();

            Booking last = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(
                    itemId, now, BookingStatus.APPROVED);

            Booking next = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                    itemId, now, BookingStatus.APPROVED);

            if (last != null) {
                itemWithBookingsDto.setLastBooking(new BookingShortDto(last.getId(), last.getBooker().getId()));
            }
            if (next != null) {
                itemWithBookingsDto.setNextBooking(new BookingShortDto(next.getId(), next.getBooker().getId()));
            }
        }

        return itemWithBookingsDto;
    }

    @Override
    public List<ItemWithBookingsDto> getUsersItems(long userId) {
        log.info("Запрос на получение всех вещей пользователя с id = {}", userId);

        List<Item> items = itemRepository.findByOwnerId(userId);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        List<Booking> userBookings = bookingRepository.findByItemIdsAndStatusIsApprovedOrderByStartTimeAsc(itemIds);
        Map<Long, List<Booking>> userBookingsByItemsId = userBookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        List<Comment> itemComments = commentRepository.findByItemIds(itemIds);
        Map<Long, List<Comment>> commentsByItemId = itemComments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();

        return items.stream()
                .map(item -> {

                    List<Booking> bookings = userBookingsByItemsId.getOrDefault(item.getId(), List.of());

                    List<CommentDto> comments = commentsByItemId.getOrDefault(item.getId(), List.of()).stream()
                            .map(CommentMapper::mapToCommentDto).toList();

                    Booking last = bookings.stream()
                            .filter(booking -> booking.getEnd().isBefore(now))
                            .max(Comparator.comparing(Booking::getEnd))
                            .orElse(null);

                    Booking next = bookings.stream()
                            .filter(booking -> booking.getStart().isAfter(now))
                            .min(Comparator.comparing(Booking::getStart))
                            .orElse(null);

                    ItemWithBookingsDto itemWithBookingsDto = ItemMapper.mapToItemWithBookingsDto(item, last, next);
                    itemWithBookingsDto.setComments(comments);

                    return itemWithBookingsDto;

                }).toList();

    }


    @Override
    public List<ItemDto> getItemsByText(long userId, String text) {
        log.info("Запрос на поиск доступных вещей по тексту = '{}'", text);
        if (text == null || text.isBlank()) {
            log.warn("Поисковый текст пуст.");
            return List.of();
        }
        return itemRepository.findByText(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteItem(long userId, long itemId) {
        log.info("Запрос на удаление вещи с id = {} пользователем с id = {}", itemId, userId);
        Item item = checkItemExisting(itemId);
        checkItemOwner(userId, item);
        itemRepository.delete(item);
        log.info("Вещь с id = {} успешно удалена.", itemId);
    }

    @Override
    @Transactional
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User user = checkUserExisting(userId);
        Item item = checkItemExisting(itemId);

        boolean hasPastBooking = bookingRepository.existsByItem_idAndBooker_idAndEndBeforeAndStatus(
                itemId, userId, LocalDateTime.now(), BookingStatus.APPROVED);

        if (!hasPastBooking) {
            throw new CommentBeforeBookingEndException("Оставить отзыв можно только после аренды");
        }

        Comment comment = CommentMapper.mapToComment(commentDto, item, user);
        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }

    private void checkItemOwner(long userId, Item itemToCheck) {
        if (itemToCheck.getOwner() == null || itemToCheck.getOwner().getId() != userId) {
            log.warn("Пользователь с id = {} не является владельцем вещи с id = {}.", userId, itemToCheck.getId());
            throw new NotItemOwnerException("Вы не являетесь владельцем этой вещи.");
        }
    }

    private Item checkItemExisting(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    private User checkUserExisting(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}
