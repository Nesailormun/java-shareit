package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    private long itemId;
    @FutureOrPresent(message = "Дата начала бронирования не должна быть в прошлом.")
    @NotNull
    private LocalDateTime start;
    @Future(message = "Дата окончания бронирования не должна быть в прошлом.")
    @NotNull
    private LocalDateTime end;

    @AssertTrue(message = "Дата начала бронирования должна быть раньше даты окончания.")
    private boolean isStartBeforeEnd() {
        return start.isBefore(end);
    }
}
