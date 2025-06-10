package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailValidationException;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setName("Иван");
        userDto.setEmail("ivan@example.com");
    }

    @Test
    void createUserSuccessTest() {
        UserDto created = userService.createUser(userDto);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    void createUserEmailExistsTest() {
        userService.createUser(userDto);

        assertThatThrownBy(() -> userService.createUser(userDto))
                .isInstanceOf(EmailValidationException.class);
    }

    @Test
    void getUserByIdFoundTest() {
        UserDto created = userService.createUser(userDto);
        UserDto found = userService.getUserById(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
    }

    @Test
    void getUserByIdNotFoundTest() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateUserSuccessTest() {
        UserDto created = userService.createUser(userDto);

        UserDto updateDto = new UserDto();
        updateDto.setName("Пётр");
        updateDto.setEmail("petr@example.com");

        UserDto updated = userService.updateUser(created.getId(), updateDto);

        assertThat(updated.getName()).isEqualTo("Пётр");
        assertThat(updated.getEmail()).isEqualTo("petr@example.com");
    }

    @Test
    void updateUserEmailExistsTest() {
        UserDto user1 = userService.createUser(userDto);
        UserDto user2 = new UserDto();
        user2.setName("Алексей");
        user2.setEmail("aleksey@example.com");
        UserDto created2 = userService.createUser(user2);

        UserDto update = new UserDto();
        update.setEmail(user1.getEmail());

        assertThatThrownBy(() -> userService.updateUser(created2.getId(), update))
                .isInstanceOf(EmailValidationException.class);
    }

    @Test
    void deleteUserSuccessTest() {
        UserDto created = userService.createUser(userDto);
        userService.deleteUser(created.getId());

        assertThatThrownBy(() -> userService.getUserById(created.getId()))
                .isInstanceOf(NotFoundException.class);
    }
}
