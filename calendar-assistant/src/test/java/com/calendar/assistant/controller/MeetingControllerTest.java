package com.calendar.assistant.controller;

import com.calendar.assistant.dto.BookMeetingDto;
import com.calendar.assistant.entity.Employee;
import com.calendar.assistant.entity.Meeting;
import com.calendar.assistant.model.CommonFreeSlotRequestDto;
import com.calendar.assistant.model.EmployeeMeetingRequest;
import com.calendar.assistant.model.MeetingRequest;
import com.calendar.assistant.model.ParticipantsRequest;
import com.calendar.assistant.service.MeetingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class MeetingControllerTest {

    @InjectMocks
    private MeetingController meetingController;

    @Mock
    private MeetingServiceImpl meetingService;

    private Employee employee;
    private Meeting meeting;
    private BookMeetingDto bookMeetingDto;
    private CommonFreeSlotRequestDto commonFreeSlotRequestDto;
    private ParticipantsRequest participantsRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employee = new Employee();
        employee.setId(1L);
        employee.setEmployeeId("E123");
        employee.setEmployeeName("Mathew Wade");

        meeting = new Meeting();
        meeting.setId(1L);
        meeting.setOwner(employee);

        bookMeetingDto = new BookMeetingDto(new ArrayList<>(), meeting);

        commonFreeSlotRequestDto = new CommonFreeSlotRequestDto();
        commonFreeSlotRequestDto.setEmployee1Id("E123");
        commonFreeSlotRequestDto.setEmployee2Id("E456");

        participantsRequest = new ParticipantsRequest();
        participantsRequest.setParticipant(List.of(employee));
    }

    @Test
    void testNewEmployee() {
        when(meetingService.newEmployee(any(Employee.class))).thenReturn(employee);

        ResponseEntity<Employee> response = meetingController.newEmp(employee);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(employee, response.getBody());
    }

    @Test
    void testRequestMeeting() {
        EmployeeMeetingRequest meetingRequest = new EmployeeMeetingRequest();
        meetingRequest.setEmployeeId("E123");
        MeetingRequest meetingRequest1=new MeetingRequest();
        meetingRequest.setRequestedMeeting(meetingRequest1);

        when(meetingService.requestMeeting(anyString(), any(MeetingRequest.class))).thenReturn(meeting);

        ResponseEntity<Meeting> response = meetingController.requestMeeting(meetingRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(meeting, response.getBody());
    }

    @Test
    void testFindCommonFreeSlots() {
        when(meetingService.findFreeTimeSlotsForEmployee(any(CommonFreeSlotRequestDto.class))).thenReturn(bookMeetingDto);

        ResponseEntity<BookMeetingDto> response = meetingController.findCommonFreeSlots(commonFreeSlotRequestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(bookMeetingDto, response.getBody());
    }

    @Test
    void testCheckParticipantConflicts() {
        List<Employee> conflictingEmployees = new ArrayList<>();
        conflictingEmployees.add(employee);

        when(meetingService.findParticipantsWithConflicts(any(ParticipantsRequest.class))).thenReturn(conflictingEmployees);

        ResponseEntity<List<Employee>> response = meetingController.checkParticipantConflicts(participantsRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(conflictingEmployees, response.getBody());
    }
}