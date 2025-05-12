package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

public interface UserRepository {

    User create(User user);

    User update(long userId, User user);

    User findByUserId(long userId);

    void deleteByUserId(long userId);
}
