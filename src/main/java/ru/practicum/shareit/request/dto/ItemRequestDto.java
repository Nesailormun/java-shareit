package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ItemRequestDto {

    private Long id;
    private String description;
    private Long requester;

}
