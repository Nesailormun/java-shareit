package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingRequestDto {
    private Long itemId;
    @Future(message = "Дата начала бронирования не должна быть в прошлом.")
    @NotNull
    private LocalDateTime start;
    @Future(message = "Дата окончания бронирования не должна быть в прошлом.")
    @NotNull
    private LocalDateTime end;
}
