package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
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
}
