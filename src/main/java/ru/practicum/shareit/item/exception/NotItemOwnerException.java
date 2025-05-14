package ru.practicum.shareit.item.exception;

public class NotItemOwnerException extends RuntimeException {
    public NotItemOwnerException(String message) {
        super(message);
    }
}
