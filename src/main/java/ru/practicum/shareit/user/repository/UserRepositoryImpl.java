package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.exception.EmailValidationException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> userStorage = new HashMap<>();

    @Override
    public User create(User user) {
        log.info("Обработка запроса на добавление нового пользователя.");
        checkEmail(user);
        user.setId(getNextId());
        userStorage.put(user.getId(), user);
        log.info("Пользователь с email = {} успешно создан.", user.getEmail());
        return user;
    }

    @Override
    public User update(long userId, User user) {
        log.info("Обработка запроса на обновление данных пользователя.");
        if (!userStorage.containsKey(userId)) {
            log.error("Ошибка обновления пользователя, пользователь с id = {} не найден.", userId);
            throw new NotFoundException("User с id = " + userId + " не найден.");
        }
        checkEmail(user);
        User updatedUser = userStorage.get(userId);
        if (user.getEmail() != null && !user.getEmail().equals(updatedUser.getEmail())) {
            updatedUser.setEmail(user.getEmail());
            log.debug("Изменено значение поля email на: {}.", user.getEmail());
        }
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
            log.debug("Изменено значение поля name на: {}.", user.getName());
        }

        userStorage.put(userId, updatedUser);
        log.info("Данные пользователя с id = {} успешно обновлены.", userId);
        return updatedUser;
    }

    @Override
    public User findByUserId(long userId) {
        log.info("Обработка запроса на получение данных пользователя.");
        if (!userStorage.containsKey(userId)) {
            log.error("Ошибка получения пользователя, пользователь с id = {} не найден.", userId);
            throw new NotFoundException("User с id = " + userId + " не найден.");
        }
        return userStorage.get(userId);
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

    private void checkEmail(User user) {
        userStorage.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .findFirst()
                .ifPresent(u -> {
                    if (u.getId() == user.getId()) {
                        return;
                    }
                    throw new EmailValidationException("User c email = " + user.getEmail() + " уже существует.");
                });
    }
}
