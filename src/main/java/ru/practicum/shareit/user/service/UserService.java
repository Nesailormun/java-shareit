package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto getUserById(long userId);

    UserDto addNewUser(UserDto userDto);

    UserDto updateUser(long userId, UserDto userDto);

    void deleteUser(long userId);
}
