package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    UserDto getUserById(@PathVariable long userId) {
        log.info("/user/{userId} GET Обработка запроса, userId = {}", userId);
        return userService.getUserById(userId);
    }

    @PostMapping
    UserDto createUser(@RequestBody @Valid UserDto userDto) {
        log.info("/user POST Обработка запроса, userDto = {}", userDto.toString());
        return userService.addNewUser(userDto);
    }

    @PatchMapping("/{userId}")
    UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.info("/user/{userId} PATCH Обработка запроса, userId = {}; userDto = {}", userId, userDto.toString());
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    void deleteUserById(@PathVariable long userId) {
        log.info("/user/{userId} DELETE Обработка запроса, userId = {}", userId);
        userService.deleteUser(userId);
    }
}
