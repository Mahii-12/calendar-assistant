package com.calendar.assistant.service;


import com.calendar.assistant.dto.BookMeetingDto;
import com.calendar.assistant.dto.TimeSlot;
import com.calendar.assistant.entity.Employee;
import com.calendar.assistant.entity.Meeting;
import com.calendar.assistant.exceptions.EmployeeNotFoundException;
import com.calendar.assistant.exceptions.ErrorCodes;
import com.calendar.assistant.exceptions.NotValidException;
import com.calendar.assistant.exceptions.OwnerNotFoundException;
import com.calendar.assistant.model.CommonFreeSlotRequestDto;
import com.calendar.assistant.model.MeetingRequest;
import com.calendar.assistant.model.ParticipantsRequest;
import com.calendar.assistant.repository.EmployeeRepository;
import com.calendar.assistant.repository.MeetingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Service
@AllArgsConstructor
public class MeetingServiceImpl {


    private EmployeeRepository employeeRepository;

    private MeetingRepository meetingRepository;

    public Employee newEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }


    public Meeting requestMeeting(String employeeId, MeetingRequest requestedMeeting) {
        log.info("Requesting a meeting for employee ID: {}", employeeId);
        Employee owner = employeeRepository.findByEmployeeId(requestedMeeting.getOwner())
                .orElseThrow(() -> new OwnerNotFoundException(ErrorCodes.OWNER_NOT_FOUND, "Calendar owner not found"));

        Employee requestorEmployee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(ErrorCodes.EMPLOYEE_NOT_FOUND, "Requesting employee not found"));


        Meeting meeting = new Meeting();
        meeting.setStartTime(requestedMeeting.getStartTime());
        meeting.setEndTime(requestedMeeting.getEndTime());
        meeting.setOwner(owner);
        meeting.setParticipant(requestorEmployee);
        meeting.setLocalDate(requestedMeeting.getLocalDate());
        Meeting savedMeeting = meetingRepository.save(meeting);
        log.info("Meeting successfully requested for employee ID: {}", employeeId);
        return savedMeeting;
    }

    public BookMeetingDto findFreeTimeSlotsForEmployee(CommonFreeSlotRequestDto commonFreeSlotRequestDto) {
        log.info("Finding free time slots for employees: {} and {}", commonFreeSlotRequestDto.getEmployee1Id(), commonFreeSlotRequestDto.getEmployee2Id());
        if (commonFreeSlotRequestDto.getEmployee1Id().equals(commonFreeSlotRequestDto.getEmployee2Id())) {
            throw new NotValidException(ErrorCodes.NOT_VALID, "Employee1 and Employee2 should not equal");
        }

        LocalTime startWork = LocalTime.of(7, 0);
        LocalTime endWork = LocalTime.of(19, 0);
        LocalDate requestedDay = commonFreeSlotRequestDto.getRequestedDay();

        Employee owner1 = employeeRepository.findByEmployeeId(commonFreeSlotRequestDto.getEmployee1Id())
                .orElseThrow(() -> new OwnerNotFoundException(ErrorCodes.OWNER_NOT_FOUND, "Calendar owner not found"));
        Employee owner2 = employeeRepository.findByEmployeeId(commonFreeSlotRequestDto.getEmployee2Id())
                .orElseThrow(() -> new OwnerNotFoundException(ErrorCodes.OWNER_NOT_FOUND, "Calendar owner not found"));

        List<TimeSlot> allMeetings = Stream.concat(
                        meetingRepository.findByParticipantAndDate(owner1, requestedDay).stream(),
                        meetingRepository.findByParticipantAndDate(owner2, requestedDay).stream())
                .map(meeting -> new TimeSlot(meeting.getStartTime(), meeting.getEndTime()))
                .sorted(Comparator.comparing(TimeSlot::getStart))
                .toList();


        LocalDateTime lastEndTime = LocalDateTime.of(requestedDay, startWork);
        List<TimeSlot> availableSlots = new ArrayList<>();

        for (TimeSlot meeting : allMeetings) {
            if (lastEndTime.isBefore(meeting.getStart())) {
                availableSlots.add(new TimeSlot(lastEndTime, meeting.getStart()));
            }
            lastEndTime = lastEndTime.isBefore(meeting.getEnd()) ? meeting.getEnd() : lastEndTime;
        }

        if (lastEndTime.isBefore(LocalDateTime.of(requestedDay, endWork))) {
            availableSlots.add(new TimeSlot(lastEndTime, LocalDateTime.of(requestedDay, endWork)));
        }

        List<TimeSlot> finalAvailableSlots = availableSlots.stream()
                .flatMap(slot -> Stream.iterate(slot.getStart(), start -> start.plusMinutes(30))
                        .limit(Duration.between(slot.getStart(), slot.getEnd()).toMinutes() / 30)
                        .map(start -> new TimeSlot(start, start.plusMinutes(30))))
                .collect(Collectors.toList());

        if (finalAvailableSlots.isEmpty()) {
            throw new NotValidException(ErrorCodes.NOT_VALID, "No available slots for scheduling a meeting");
        }

        TimeSlot meetingSlot = finalAvailableSlots.get(0);
        log.info("Selected meeting slot: {} to {}", meetingSlot.getStart(), meetingSlot.getEnd());

        MeetingRequest meetingRequest = new MeetingRequest();
        meetingRequest.setOwner(commonFreeSlotRequestDto.getEmployee1Id());
        meetingRequest.setStartTime(meetingSlot.getStart());
        meetingRequest.setEndTime(meetingSlot.getEnd());
        meetingRequest.setLocalDate(meetingSlot.getStart().toLocalDate());

        Meeting meeting = requestMeeting(commonFreeSlotRequestDto.getEmployee2Id(), meetingRequest);
        log.info("Meeting successfully scheduled between {} and {}", owner1.getEmployeeId(), owner2.getEmployeeId());
        return new BookMeetingDto(finalAvailableSlots, meeting);
    }


    public List<Employee> findParticipantsWithConflicts(ParticipantsRequest meetingRequest) {
        log.info("Finding participants with conflicts for meeting request: {}", meetingRequest);
        return meetingRequest.getParticipant().stream()
                .map(participant -> employeeRepository.findByEmployeeId(participant.getEmployeeId())
                        .orElseThrow(() -> new EmployeeNotFoundException(ErrorCodes.EMPLOYEE_NOT_FOUND,
                                "Employee not found: " + participant.getEmployeeId())))
                .filter(participant -> hasConflict(participant, meetingRequest))
                .collect(Collectors.toList());
    }

    public boolean hasConflict(Employee participant, ParticipantsRequest meetingRequest) {
        log.info("Checking for conflicts for participant: {} on date: {}", participant.getEmployeeId(), meetingRequest.getLocalDate());
        return meetingRepository.findByParticipantAndDate(participant, meetingRequest.getLocalDate()).stream()
                .anyMatch(existingMeeting -> isTimeConflict(existingMeeting, meetingRequest));
    }

    private boolean isTimeConflict(Meeting existingMeeting, ParticipantsRequest meetingRequest) {
        LocalDateTime startTime = meetingRequest.getStartTime();
        LocalDateTime endTime = meetingRequest.getEndTime();
        return startTime.isEqual(existingMeeting.getStartTime()) ||
                (startTime.isAfter(existingMeeting.getStartTime()) && startTime.isBefore(existingMeeting.getEndTime())) ||
                (endTime.isAfter(existingMeeting.getStartTime()) && endTime.isBefore(existingMeeting.getEndTime())) ||
                (startTime.isBefore(existingMeeting.getStartTime()) && endTime.isAfter(existingMeeting.getEndTime()));
    }


}


