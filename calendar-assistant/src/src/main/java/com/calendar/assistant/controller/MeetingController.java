package com.calendar.assistant.controller;

import com.calendar.assistant.dto.BookMeetingDto;
import com.calendar.assistant.entity.Employee;
import com.calendar.assistant.entity.Meeting;
import com.calendar.assistant.model.CommonFreeSlotRequestDto;
import com.calendar.assistant.model.EmployeeMeetingRequest;
import com.calendar.assistant.model.ParticipantsRequest;
import com.calendar.assistant.service.MeetingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("api/v1/calendar")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingServiceImpl meetingService;


    @PostMapping("/employee-create")
    public ResponseEntity<Employee> newEmp(@RequestBody Employee employee) {
        return new ResponseEntity<>(meetingService.newEmployee(employee), HttpStatus.CREATED);
    }


    @PostMapping("/request-meeting")
    public ResponseEntity<Meeting> requestMeeting(@RequestBody EmployeeMeetingRequest meetingRequest) {
        Meeting savedMeeting = meetingService.requestMeeting(
                meetingRequest.getEmployeeId(),
                meetingRequest.getRequestedMeeting()
        );
        return new ResponseEntity<>(savedMeeting, HttpStatus.CREATED);
    }

    @PostMapping("/find-common-slots")
    public ResponseEntity<BookMeetingDto> findCommonFreeSlots(@RequestBody CommonFreeSlotRequestDto requestDto) {
        BookMeetingDto bookMeetingDto = meetingService.findFreeTimeSlotsForEmployee(requestDto);
        return new ResponseEntity<>(bookMeetingDto, HttpStatus.CREATED);
    }

    @GetMapping("/get-conflicts")
    public ResponseEntity<List<Employee>> checkParticipantConflicts(@RequestBody ParticipantsRequest participantsRequest) {
        List<Employee> conflictingParticipants = meetingService.findParticipantsWithConflicts(participantsRequest);
        return ResponseEntity.ok(conflictingParticipants);
    }

}
