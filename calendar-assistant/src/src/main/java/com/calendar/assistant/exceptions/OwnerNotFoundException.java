package com.calendar.assistant.exceptions;

import lombok.Getter;

@Getter
public class OwnerNotFoundException extends RuntimeException{

    private final ErrorCodes errorCodes;

    public OwnerNotFoundException(ErrorCodes errorCodes, String message) {
        super(message);
        this.errorCodes = errorCodes;
    }
}
