package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {

    private Long id;

    @Size(max = 255, message = "Имя пользователя не должно превышать 255 символов")
    private String name;

    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Неверный формат email.")
    @Size(max = 255, message = "Email пользователя не должен превышать 255 символов")
    private String email;
}