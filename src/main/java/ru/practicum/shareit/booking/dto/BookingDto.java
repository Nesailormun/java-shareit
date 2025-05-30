package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDto {
    private Long id;
    private ItemShortDto item;
    private UserShortDto booker;
    private LocalDateTime start;
    private LocalDateTime end;
    private String status;
}
