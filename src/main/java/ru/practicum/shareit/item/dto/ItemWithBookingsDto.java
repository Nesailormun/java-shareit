package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

@Getter
@Setter
public class ItemWithBookingsDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long requestId;
    private List<CommentDto> comments;

    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;

}
