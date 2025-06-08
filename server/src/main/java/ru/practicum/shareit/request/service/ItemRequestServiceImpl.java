package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestDto dto) {
        log.info("Создание нового запроса от пользователя с id ={}", userId);

        User user = checkAndReturnUser(userId);

        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());

        ItemRequest saved = requestRepository.save(request);
        log.info("Запрос успешно создан с id ={}", saved.getId());

        return ItemRequestMapper.toItemRequestDto(saved);
    }


    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        log.info("Получение запросов пользователя с id={}", userId);

        List<ItemRequestDto> requests = requestRepository.findByRequesterIdOrderByCreatedDesc(userId)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        log.info("Найдено {} запросов для пользователя {}", requests.size(), userId);
        return requests;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        log.info("Получение запросов других пользователей, запрашивает пользователь с id ={}, from={}, size={}", userId, from, size);

        List<ItemRequestDto> requests = requestRepository
                .findByRequesterIdNotOrderByCreatedDesc(userId, (Pageable) PageRequest.of(from / size, size))
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        log.info("Найдено {} чужих запросов", requests.size());
        return requests;
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        log.info("Получение запроса с id ={} для пользователя c id ={}", requestId, userId);

        checkAndReturnUser(userId);
        ItemRequest request = checkAndReturnItemRequest(requestId);

        log.info("Запрос с id={} успешно найден", requestId);
        return ItemRequestMapper.toItemRequestDto(request);
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
}
