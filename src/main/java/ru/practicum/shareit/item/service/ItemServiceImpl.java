package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.exception.NotItemOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        log.info("Обработка запроса на добавление нового предмета пользователем с id = {}", userId);
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));

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
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена."));
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
    public ItemDto getItemById(long userId, long itemId) {
        log.info("Запрос на получение вещи с id = {} пользователем с id = {}", itemId, userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена."));
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> getUsersItems(long userId) {
        log.info("Запрос на получение всех вещей пользователя с id = {}", userId);
        return itemRepository.findByOwnerId(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> getItemsByText(long userId, String text) {
        log.info("Запрос на поиск доступных вещей по тексту = '{}'", text);
        if (text == null || text.isBlank()) {
            log.warn("Поисковый текст пуст, возвращаем пустой список.");
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
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена."));
        checkItemOwner(userId, item);
        itemRepository.delete(item);
        log.info("Вещь с id = {} успешно удалена.", itemId);
    }

    private void checkItemOwner(long userId, Item itemToCheck) {
        if (itemToCheck.getOwner() == null || itemToCheck.getOwner().getId() != userId) {
            log.warn("Пользователь с id = {} не является владельцем вещи с id = {}.", userId, itemToCheck.getId());
            throw new NotItemOwnerException("Вы не являетесь владельцем этой вещи.");
        }
    }
}
