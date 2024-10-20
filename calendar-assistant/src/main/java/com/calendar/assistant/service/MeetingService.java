package com.calendar.assistant.service;

import com.calendar.assistant.entity.Employee;
import com.calendar.assistant.entity.Meeting;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public interface MeetingService {
    public Employee bookMeeting(String employeeId, Meeting meeting);

    public List<LocalDateTime> findFreeSlots(String emp1Id, String emp2Id, Duration duration);

    public List<String> findConflicts(Meeting requestedMeeting, List<String> participantIds);
}
