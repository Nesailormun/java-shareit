package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestBody ItemRequestDto dto) {
        log.info("Получен запрос POST /requests от пользователя id={}", userId);
        ItemRequestDto createdRequest = itemRequestService.createRequest(userId, dto);
        log.info("Создан запрос с id={} от пользователя id={}", createdRequest.getId(), userId);
        return createdRequest;
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос GET /requests для пользователя id={}", userId);
        List<ItemRequestDto> requests = itemRequestService.getUserRequests(userId);
        log.info("Возвращено {} запросов для пользователя id={}", requests.size(), userId);
        return requests;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос GET /requests/all от пользователя id={}, from={}, size={}", userId, from, size);
        List<ItemRequestDto> requests = itemRequestService.getAllRequests(userId, from, size);
        log.info("Возвращено {} запросов от других пользователей", requests.size());
        return requests;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long requestId) {
        log.info("Получен запрос GET /requests/{} от пользователя id={}", requestId, userId);
        ItemRequestDto requestDto = itemRequestService.getRequestById(userId, requestId);
        log.info("Возвращен запрос с id={} для пользователя id={}", requestId, userId);
        return requestDto;
    }
}
