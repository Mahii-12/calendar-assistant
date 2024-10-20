package com.calendar.assistant.exceptions;

import lombok.Getter;

@Getter
public class NotValidException extends RuntimeException {
    private final ErrorCodes errorCodes;

    public NotValidException(ErrorCodes errorCodes, String message) {
        super(message);
        this.errorCodes = errorCodes;
    }
}
