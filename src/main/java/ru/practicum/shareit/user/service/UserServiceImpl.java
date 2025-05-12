package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = userRepository.findByUserId(userId);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto addNewUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        User createdUser = userRepository.create(user);
        return UserMapper.mapToUserDto(createdUser);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        User updatedUser = userRepository.update(userId, user);
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteByUserId(userId);
    }
}
