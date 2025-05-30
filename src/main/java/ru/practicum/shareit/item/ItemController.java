package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemWithBookingsDto> getUsersItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("/items/ GET выполнение запроса. userId={}", userId);
        return itemService.getUsersItems(userId);
    }

    @GetMapping("/{itemId}")
    ItemWithBookingsDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("/items/{itemId} GET выполнение запроса. userId={}; itemId = {}", userId, itemId);
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    List<ItemDto> getItemsByText(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam("text") String text) {
        log.info("/items/search GET выполнение запроса. userId={}; text = '{}'", userId, text);
        return itemService.getItemsByText(userId, text);
    }

    @PostMapping
    ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("/items POST выполнение запроса. userId={}; itemDto = {}", userId, itemDto.toString());
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        log.info("/items/{itemId} PATCH выполнение запроса. userId={}; itemId = {}; itemDto = {}", userId, itemId, itemDto.toString());
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("/items/{itemId} DELETE выполнение запроса. userId={}; itemId = {}", userId, itemId);
        itemService.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody @Valid CommentDto commentDto) {
        log.info("/items/{itemId}/comment POST выполнение запроса от userId = {}", userId);
        return itemService.addComment(userId, itemId, commentDto);
    }


}
