package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailValidationException;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUserTest() throws Exception {
        UserDto requestDto = new UserDto();
        requestDto.setName("Иван");
        requestDto.setEmail("ivan@example.com");

        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setName("Иван");
        responseDto.setEmail("ivan@example.com");

        Mockito.when(userService.createUser(any(UserDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.email").value("ivan@example.com"));

        verify(userService).createUser(any(UserDto.class));
    }

    @Test
    void getUserByIdTest() throws Exception {
        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setName("Иван");
        responseDto.setEmail("ivan@example.com");

        Mockito.when(userService.getUserById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.email").value("ivan@example.com"));

        verify(userService).getUserById(1L);
    }

    @Test
    void updateUserTest() throws Exception {
        UserDto updateDto = new UserDto();
        updateDto.setName("Пётр");
        updateDto.setEmail("petr@example.com");

        UserDto responseDto = new UserDto();
        responseDto.setId(1L);
        responseDto.setName("Пётр");
        responseDto.setEmail("petr@example.com");

        Mockito.when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Пётр"))
                .andExpect(jsonPath("$.email").value("petr@example.com"));

        verify(userService).updateUser(eq(1L), any(UserDto.class));
    }

    @Test
    void getUserByIdNotFoundTest() throws Exception {
        Mockito.when(userService.getUserById(99L))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/users/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUserEmailConflictTest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Иван");
        userDto.setEmail("existing@example.com");

        Mockito.when(userService.createUser(any(UserDto.class)))
                .thenThrow(new EmailValidationException("Email уже используется"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk());

        verify(userService).deleteUser(1L);
    }
}
