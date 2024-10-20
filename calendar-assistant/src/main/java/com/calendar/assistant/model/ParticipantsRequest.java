package com.calendar.assistant.model;

import com.calendar.assistant.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantsRequest {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDate localDate;
    private List<Employee> participant;
}
