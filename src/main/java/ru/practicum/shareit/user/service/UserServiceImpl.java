package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getUserById(long userId) {
        log.info("Обработка запроса на получение данных пользователя.");
        User user = userRepository.findByUserId(userId);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto addNewUser(UserDto userDto) {
        log.info("Обработка запроса на добавление нового пользователя.");
        User user = UserMapper.mapToUser(userDto);
        User createdUser = userRepository.create(user);
        return UserMapper.mapToUserDto(createdUser);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        log.info("Обработка запроса на обновление данных пользователя c id = {}.", userId);
        userRepository.findByUserId(userId);
        User user = UserMapper.mapToUser(userDto);
        User updatedUser = userRepository.update(userId, user);
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public void deleteUser(long userId) {
        log.info("Обработка запроса на удаление пользователя с id = {}.", userId);
        userRepository.deleteByUserId(userId);
    }
}
