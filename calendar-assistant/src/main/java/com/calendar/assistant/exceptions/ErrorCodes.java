package com.calendar.assistant.exceptions;

import lombok.Getter;

@Getter
public enum ErrorCodes {
    EMPLOYEE_NOT_FOUND(1),
    OWNER_NOT_FOUND(2),
    BUSY_DURATION(3),
    NOT_VALID(4);

    private final int code;

    ErrorCodes(int code) {
        this.code = code;
    }
}
