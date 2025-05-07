package ru.practicum.shareit.booking.dto;


import lombok.Data;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Data
public class BookingDto {

    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long item;
    private Status status;
}
