package ru.practicum.shareit.booking;

import ch.qos.logback.core.status.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Booking {

    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long item;
    private long booker;
    private Status status;
}
