package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> itemStorage = new HashMap<>();

    @Override
    public Item create(long userId, Item item) {
        item.setId(getNextId());
        itemStorage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(long userId, long itemId, Item item) {
        Item updatedItem = itemStorage.get(itemId);
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
            log.debug("Название обновлено на {}.", item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
            log.debug("Описание обновлено на {}.", item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
            log.debug("Доступность обновлена на {}.", item.getAvailable());
        }
        itemStorage.put(itemId, updatedItem);
        log.info("Вещь с id = {} успешно обновлена", itemId);
        return updatedItem;
    }

    @Override
    public Item findById(long itemId) {
        Item item = itemStorage.get(itemId);
        if (item == null) {
            log.error("Вещь с id = {} не найдена", itemId);
            throw new NotFoundException("Item с id = " + itemId + " не найден.");
        }
        return item;
    }

    @Override
    public List<Item> findUsersItems(long userId) {
        return itemStorage.values()
                .stream()
                .filter(item -> item.getOwner() == userId)
                .toList();
    }

    @Override
    public List<Item> findByText(String text) {
        String lowerCaseText = text.toLowerCase();
        return itemStorage.values()
                .stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toLowerCase().contains(lowerCaseText) ||
                                item.getDescription().toLowerCase().contains(lowerCaseText)))
                .toList();
    }

    @Override
    public void deleteByItemId(long userId, long itemId) {
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
