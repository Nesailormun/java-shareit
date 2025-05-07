package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ItemRequestDto {

    private long id;
    private String description;
    private long requestor;
    private Timestamp created;

}
