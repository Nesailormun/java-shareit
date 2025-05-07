package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailValidationException;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> userStorage = new HashMap<>();

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Обработка запроса на добавление нового пользователя.");
        checkEmail(userDto);
        userDto.setId(getNextId());
        userStorage.put(userDto.getId(), UserMapper.mapToUser(userDto));
        log.info("Пользователь с email = {} успешно создан.", userDto.getEmail());
        return userDto;
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        log.info("Обработка запроса на обновление данных пользователя.");
        if (!userStorage.containsKey(userId)) {
            log.error("Ошибка обновления пользователя, пользователь с id = {} не найден.", userId);
            throw new NotFoundException("User с id = " + userId + " не найден.");
        }
        checkEmail(userDto);
        User updatedUser = userStorage.get(userId);
        if (userDto.getEmail() != null) {
            updatedUser.setEmail(userDto.getEmail());
            log.debug("Изменено значение поля email на: {}.", userDto.getEmail());
        }
        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
            log.debug("Изменено значение поля name на: {}.", userDto.getName());
        }
        userStorage.put(userId, updatedUser);
        log.info("Данные пользователя с email = {} успешно обновлены.", updatedUser.getEmail());
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public UserDto findByUserId(long userId) {
        log.info("Обработка запроса на получение данных пользователя.");
        if (!userStorage.containsKey(userId)) {
            log.error("Ошибка получения пользователя, пользователь с id = {} не найден.", userId);
            throw new NotFoundException("User с id = " + userId + " не найден.");
        }
        return UserMapper.mapToUserDto(userStorage.get(userId));
    }

    @Override
    public void deleteByUserId(long userId) {
        log.info("Обработка запроса на удаление пользователя с id = {}.", userId);
        userStorage.remove(userId);
        log.info("Пользователь с id = {} удален.", userId);
    }

    private long getNextId() {
        long currentMaxId = userStorage.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void checkEmail(UserDto userDto) {
        userStorage.values().stream()
                .filter(u -> u.getEmail().equals(userDto.getEmail()))
                .findFirst()
                .ifPresent(u -> {
                    throw new EmailValidationException("User c email = " + u.getEmail() + " уже существует.");
                });
    }

}
