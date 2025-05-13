package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item create(long userId, Item item);

    Item update(long userId, long itemId, Item item);

    Item findById(long itemId);

    List<Item> findUsersItems(long userId);

    List<Item> findByText(String text);

    void deleteByItemId(long userId, long itemId);
}
