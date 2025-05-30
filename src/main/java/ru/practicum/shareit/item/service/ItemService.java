package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    ItemWithBookingsDto getItemById(long userId, long itemId);

    List<ItemWithBookingsDto> getUsersItems(long userId);

    List<ItemDto> getItemsByText(long userId, String text);

    void deleteItem(long userId, long itemId);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);

}
