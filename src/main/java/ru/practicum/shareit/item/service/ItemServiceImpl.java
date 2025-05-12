package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        userRepository.findByUserId(userId);
        itemDto.setOwner(userId);
        Item item = ItemMapper.mapToItem(itemDto);
        Item createdItem = itemRepository.create(userId, item);
        return ItemMapper.mapToItemDto(createdItem);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());

        Item updatedItem = itemRepository.update(userId, itemId, item);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        Item item = itemRepository.findById(userId, itemId);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> getUsersItems(long userId) {
        return itemRepository.findUsersItems(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByText(long userId, String text) {
        return itemRepository.findByText(userId, text).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        itemRepository.deleteByItemId(userId, itemId);
    }
}
