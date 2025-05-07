package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDto> getUsersItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getUsersItems(userId);
    }

    @GetMapping("/{itemId}")
    ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    List<ItemDto> getItemsByText(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam("text") String text) {
        return itemService.getItemsByText(userId, text);
    }

    @PostMapping
    ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid ItemDto itemDto) {
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }
}
