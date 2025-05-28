package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import ru.practicum.shareit.user.exception.EmailValidationException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        checkEmail(userDto);
        User user = UserMapper.mapToNewUser(userDto);
        User saved = userRepository.save(user);
        log.info("Пользователь с email = {} успешно создан.", user.getEmail());
        return UserMapper.mapToUserDto(saved);
    }

    @Override
    public UserDto getUserById(Long userId) {
        log.info("Поиск user по id = {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Ошибка. User c id = {} не найден.", userId);
                    return new NotFoundException("User c id =: " + userId + " не найден");
                });
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = UserMapper.mapToUser(getUserById(userId));
        if (userDto.getEmail() != null && userDto.getId() != user.getId()) {
            checkEmail(userDto);
            user.setEmail(userDto.getEmail());
            log.debug("Изменено значение поля email на: {}.", user.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
            log.debug("Изменено значение поля name на: {}.", user.getName());
        }

        User updated = userRepository.save(user);
        log.info("Данные пользователя с id = {} успешно обновлены.", userId);
        return UserMapper.mapToUserDto(updated);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.info("User с id = {} удален.", userId);
    }

    private void checkEmail(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.error("Ошибка. User c email {} уже существует.", userDto.getEmail());
            throw new EmailValidationException("User c email = " + userDto.getEmail() + " уже существует.");
        }
    }
}