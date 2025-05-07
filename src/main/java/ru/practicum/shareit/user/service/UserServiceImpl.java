package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getUserById(long userId) {
        return userRepository.findByUserId(userId);
    }

    @Override
    public UserDto addNewUser(UserDto userDto) {
        return userRepository.create(userDto);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        return userRepository.update(userId, userDto);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteByUserId(userId);
    }
}
