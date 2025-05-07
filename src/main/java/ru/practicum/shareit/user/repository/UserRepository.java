package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserRepository {

    UserDto create(UserDto userDto);

    UserDto update(long userId, UserDto userDto);

    UserDto findByUserId(long userId);

    void deleteByUserId(long userId);
}
