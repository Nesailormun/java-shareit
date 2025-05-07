package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> itemStorage = new HashMap<>();
    private final UserRepository userRepository;

    public ItemRepositoryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        log.info("Обработка запроса на добавление нового предмета.");
        userRepository.findByUserId(userId);
        itemDto.setId(getNextId());
        itemDto.setOwner(userId);
        itemStorage.put(itemDto.getId(), ItemMapper.mapToItem(itemDto));
        return itemDto;
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        log.info("Запрос на обновление данных вещи с id = {} пользователем с id = {}.", itemId, userId);
        if (!itemStorage.containsKey(itemId)) {
            log.error("Ошибка обновления. Вещь с id = {} не найдена.", itemId);
            throw new NotFoundException("Вещь с id = " + itemId + " не найдена.");
        }
        Item updatedItem = itemStorage.get(itemId);
        if (updatedItem.getOwner() != userId) {
            log.warn("Пользователь с id = {} не является владельцем вещи с id = {}.", userId, itemId);
            throw new NotFoundException("Вы не являетесь владельцем этой вещи.");
        }

        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
            log.debug("Название обновлено на {}.", itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
            log.debug("Описание обновлено на {}.", itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
            log.debug("Доступность обновлена на {}.", itemDto.getAvailable());
        }

        itemStorage.put(itemId, updatedItem);
        log.info("Вещь с id = {} успешно обновлена", itemId);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public ItemDto findById(long userId, long itemId) {
        log.info("Запрос на получение вещи с id = {} пользователем с id = {}", itemId, userId);
        if (!itemStorage.containsKey(itemId)) {
            log.error("Вещь с id = {} не найдена", itemId);
            throw new NotFoundException("Item с id = " + itemId + "не найден.");
        }
        return ItemMapper.mapToItemDto(itemStorage.get(itemId));
    }

    @Override
    public List<ItemDto> findUsersItems(long userId) {
        log.info("Запрос на получение всех вещей пользователя с id = {}", userId);
        return itemStorage.values()
                .stream()
                .filter(item -> item.getOwner() == userId)
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> findByText(long userId, String text) {
        log.info("Запрос на получение доступных вещей по тексту = '{}'", text);
        if (text == null || text.isBlank()) {
            log.error("Текст пуст.");
            return List.of();
        }
        String lowerCaseText = text.toLowerCase();
        return itemStorage.values()
                .stream()
                .filter(item -> item.isAvailable() && (item.getDescription().toLowerCase().contains(lowerCaseText)
                        || item.getName().toLowerCase().contains(lowerCaseText)))
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public void deleteByItemId(long userId, long itemId) {
        log.info("Запрос на удаление вещи с id = {} пользователем с id = {}", itemId, userId);
        itemStorage.remove(itemId);
        log.info("Вещь с id = {} удалена", itemId);
    }

    private long getNextId() {
        long currentMaxId = itemStorage.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
