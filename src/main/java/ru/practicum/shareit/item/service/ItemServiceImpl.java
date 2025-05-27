package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.NotItemOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        log.info("Обработка запроса на добавление нового предмета.");
        userRepository.findByUserId(userId);
        itemDto.setOwner(userId);
        Item item = ItemMapper.mapToItem(itemDto);
        Item createdItem = itemRepository.create(userId, item);
        log.info("Предмет item = {}, успешно создан", createdItem.toString());
        return ItemMapper.mapToItemDto(createdItem);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        log.info("Запрос на обновление данных вещи с id = {} пользователем с id = {}.", itemId, userId);
        Item itemToCheck = itemRepository.findById(itemId);
        checkItemOwner(userId, itemToCheck);
        Item item = ItemMapper.mapToItem(itemDto);
        Item updatedItem = itemRepository.update(userId, itemId, item);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        log.info("Запрос на получение вещи с id = {} пользователем с id = {}", itemId, userId);
        Item item = itemRepository.findById(itemId);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> getUsersItems(long userId) {
        log.info("Запрос на получение всех вещей пользователя с id = {}", userId);
        return itemRepository.findUsersItems(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByText(long userId, String text) {
        log.info("Запрос на получение доступных вещей по тексту = '{}'", text);
        if (text == null || text.isBlank()) {
            log.warn("Поисковый текст пуст.");
            return List.of();
        }
        return itemRepository.findByText(text).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        log.info("Запрос на удаление вещи с id = {} пользователем с id = {}", itemId, userId);
        Item item = itemRepository.findById(itemId);
        checkItemOwner(userId, item);
        itemRepository.deleteByItemId(userId, itemId);
    }

    private void checkItemOwner(long userId, Item itemToCheck) {
        if (itemToCheck.getOwner().getId() != userId) {
            log.warn("Пользователь с id = {} не является владельцем вещи с id = {}.", userId, itemToCheck.getId());
            throw new NotItemOwnerException("Вы не являетесь владельцем этой вещи.");
        }
    }
}

