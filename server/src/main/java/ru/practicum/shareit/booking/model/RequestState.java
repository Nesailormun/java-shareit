package ru.practicum.shareit.booking.model;

public enum RequestState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static RequestState from(String state) {
        try {
            return RequestState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неизвестное значение запроса state: " + state);
        }
    }
}
