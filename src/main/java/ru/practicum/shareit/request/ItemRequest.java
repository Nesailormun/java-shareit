package ru.practicum.shareit.request;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ItemRequest {

    private long id;
    private String description;
    private long requestor;
    private Timestamp created;

}
