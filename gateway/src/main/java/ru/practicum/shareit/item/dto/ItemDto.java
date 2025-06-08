package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {

    private Long id;

    @NotBlank(message = "Название вещи не может быть пустым")
    @Size(max = 255, message = "Название не должно превышать 255 символов")
    private String name;

    @NotBlank(message = "Описание вещи не может быть пустым")
    @Size(max = 512, message = "Описание не должно превышать 512 символов")
    private String description;

    @NotNull(message = "Статус доступности должен быть указан")
    private Boolean available;

    private Long requestId; // id запроса, если вещь создается по запросу, может быть null

}
