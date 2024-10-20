package com.calendar.assistant.exceptions;

import com.calendar.assistant.util.GenericErrorResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<GenericErrorResponse> handleEmployeeException(EmployeeNotFoundException ex) {
        return createResponseEntity(ex.getErrorCodes(), ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OwnerNotFoundException.class)
    public ResponseEntity<GenericErrorResponse> handleOwnerException(OwnerNotFoundException ex) {
        return createResponseEntity(ex.getErrorCodes(), ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusyDurationException.class)
    public ResponseEntity<GenericErrorResponse> handleBusyDurationException(BusyDurationException ex) {
        return createResponseEntity(ex.getErrorCodes(), ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotValidException.class)
    public ResponseEntity<GenericErrorResponse> handleNotValidException(NotValidException ex) {
        return createResponseEntity(ex.getErrorCodes(), ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<GenericErrorResponse> createResponseEntity(ErrorCodes errorCode, String message, HttpStatus status) {
        GenericErrorResponse errorResponse = new GenericErrorResponse(errorCode.getCode(), message);
        return new ResponseEntity<>(errorResponse, status);
    }

}
