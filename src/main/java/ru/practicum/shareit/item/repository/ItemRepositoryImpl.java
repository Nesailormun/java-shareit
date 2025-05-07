package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> itemsStorage;

    public ItemRepositoryImpl(Map<Long, Item> itemsStorage) {
        this.itemsStorage = itemsStorage;
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        return null;
    }

    @Override
    public void update(long userId, long itemId, ItemDto itemDto) {

    }

    @Override
    public List<ItemDto> findAll(long userId) {
        return List.of();
    }

    @Override
    public List<ItemDto> findByText(long userId, String text) {
        return List.of();
    }

    @Override
    public void delete(long userId, long itemId) {

    }
}
