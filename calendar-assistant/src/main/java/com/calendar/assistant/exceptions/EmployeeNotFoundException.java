package com.calendar.assistant.exceptions;

import lombok.Getter;

@Getter
public class EmployeeNotFoundException extends RuntimeException {
    private final ErrorCodes errorCodes;

    public EmployeeNotFoundException(ErrorCodes errorCodes, String message) {
        super(message);
        this.errorCodes = errorCodes;
    }
}
