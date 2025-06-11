package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestDto dto) {
        log.info("Создание нового запроса от пользователя с id ={}", userId);
        User user = checkAndReturnUser(userId);
        ItemRequest request = ItemRequestMapper.toItemRequest(dto, user);
        ItemRequest saved = requestRepository.save(request);
        log.info("Запрос успешно создан с id ={}", saved.getId());

        return ItemRequestMapper.toItemRequestDto(saved);
    }


    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        log.info("Получение запросов пользователя с id={}", userId);

        checkAndReturnUser(userId);
        List<ItemRequest> requests = requestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        log.info("Найдено {} запросов для пользователя {}", requests.size(), userId);
        return getItemRequestsWithItems(requests);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        log.info("Получение запросов других пользователей, запрашивает пользователь с id ={}, from={}, size={}", userId, from, size);

        checkAndReturnUser(userId);
        List<ItemRequest> requests = requestRepository
                .findByRequesterIdNotOrderByCreatedDesc(userId, PageRequest.of(from / size, size));

        log.info("Найдено {} чужих запросов", requests.size());

        return getItemRequestsWithItems(requests);
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        log.info("Получение запроса с id ={} для пользователя c id ={}", requestId, userId);

        checkAndReturnUser(userId);
        ItemRequest request = checkAndReturnItemRequest(requestId);
        log.info("Запрос с id={} успешно найден", requestId);
        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(request);
        List<ItemDto> itemDtos = itemRepository.findByRequestIdOrderByRequestIdDesc(requestId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
        dto.setItems(itemDtos);
        return dto;
    }

    private ItemRequest checkAndReturnItemRequest(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Запрос с id ={} не найден", requestId);
                    return new NotFoundException("Запрос не найден");
                });
    }

    private User checkAndReturnUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id={} не найден", userId);
                    return new NotFoundException("Пользователь не найден");
                });
    }

    private List<ItemRequestDto> getItemRequestsWithItems(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> items = itemRepository.findAllByRequestIdIn(requestIds);

        Map<Long, List<Item>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> {
                    List<ItemDto> requestItems = itemsByRequestId.getOrDefault(request.getId(), List.of()).stream()
                            .map(ItemMapper::mapToItemDto).toList();
                    ItemRequestDto requestWithItems = ItemRequestMapper.toItemRequestDto(request);
                    requestWithItems.setItems(requestItems);
                    return requestWithItems;
                })
                .toList();
    }

}
