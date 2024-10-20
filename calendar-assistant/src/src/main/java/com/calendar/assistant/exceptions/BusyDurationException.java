package com.calendar.assistant.exceptions;

import lombok.Getter;

@Getter
public class BusyDurationException extends RuntimeException{
    private final ErrorCodes errorCodes;

    public BusyDurationException(ErrorCodes errorCodes, String message) {
        super(message);
        this.errorCodes = errorCodes;
    }
}
