package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemRepository {

    ItemDto create(long userId, ItemDto itemDto);

    void update(long userId, long itemId, ItemDto itemDto);

    List<ItemDto> findAll(long userId);

    List<ItemDto> findByText(long userId, String text);

    void delete(long userId, long itemId);
}
