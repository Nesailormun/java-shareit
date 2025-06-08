package ru.practicum.shareit.item.exception;

public class CommentBeforeBookingEndException extends RuntimeException {
    public CommentBeforeBookingEndException(String message) {
        super(message);
    }
}
