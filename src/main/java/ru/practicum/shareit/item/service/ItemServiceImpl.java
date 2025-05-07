package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        return itemRepository.create(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        return itemRepository.update(userId, itemId, itemDto);
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        return itemRepository.findById(userId, itemId);
    }

    @Override
    public List<ItemDto> getUsersItems(long userId) {
        return itemRepository.findUsersItems(userId);
    }

    @Override
    public List<ItemDto> getItemsByText(long userId, String text) {
        return itemRepository.findByText(userId, text);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        itemRepository.deleteByItemId(userId, itemId);
    }
}
