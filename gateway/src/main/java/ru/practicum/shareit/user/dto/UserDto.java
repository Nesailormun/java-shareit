package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(max = 255, message = "Имя пользователя не должно превышать 255 символов")
    private String name;

    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Неверный формат email.")
    @NotBlank(message = "Email не может быть пустым")
    @Size(max = 255, message = "Email пользователя не должен превышать 255 символов")
    private String email;
}
