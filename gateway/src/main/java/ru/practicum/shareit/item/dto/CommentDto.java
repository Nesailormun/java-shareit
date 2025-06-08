package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    private Long id;

    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(max = 500, message = "Текст комментария не должен превышать 500 символов")
    private String text;

    private String authorName;

    private LocalDateTime created;

}
