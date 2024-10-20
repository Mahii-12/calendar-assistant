package com.calendar.assistant.model;

import com.calendar.assistant.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingRequest {

    private String owner;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDate localDate;
}
