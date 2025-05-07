package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemRepository {

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDto findById(long userId, long itemId);

    List<ItemDto> findUsersItems(long userId);

    List<ItemDto> findByText(long userId, String text);

    void deleteByItemId(long userId, long itemId);
}
