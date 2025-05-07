package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    ItemDto getItemById(long userId, long itemId);

    List<ItemDto> getUsersItems(long userId);

    List<ItemDto> getItemsByText(long userId, String text);

    void deleteItem(long userId, long itemId);

}
