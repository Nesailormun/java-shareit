package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemDto {

    private Long id;

    @Size(max = 255, message = "Название не должно превышать 255 символов")
    private String name;

    @Size(max = 512, message = "Описание не должно превышать 512 символов")
    private String description;

    private Boolean available;

    private Long requestId;

}
