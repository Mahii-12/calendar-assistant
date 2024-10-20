package com.calendar.assistant.model;

import com.calendar.assistant.entity.Employee;
import com.calendar.assistant.entity.Meeting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeMeetingRequest {

    private String employeeId;
    private MeetingRequest requestedMeeting;
}
