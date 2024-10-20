package com.calendar.assistant.service;

import com.calendar.assistant.dto.BookMeetingDto;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MeetingServiceImplTest {


    @InjectMocks
    private MeetingServiceImpl meetingService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private MeetingRepository meetingRepository;

    private Employee owner;
    private Employee requestor;
    private MeetingRequest meetingRequest;
    private Meeting savedMeeting;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        owner = new Employee();
        owner.setId(1L);
        owner.setEmployeeId("E123");
        owner.setEmployeeName("Mathew Wade");

        requestor = new Employee();
        requestor.setId(2L);
        requestor.setEmployeeId("E456");
        requestor.setEmployeeName("Joe Root");

        meetingRequest = new MeetingRequest();
        meetingRequest.setOwner("E123");
        meetingRequest.setStartTime(LocalDateTime.of(2024, 10, 18, 12, 0));
        meetingRequest.setEndTime(LocalDateTime.of(2024, 10, 18, 13, 0));
        meetingRequest.setLocalDate(LocalDate.of(2024, 10, 18));

        savedMeeting = new Meeting();
        savedMeeting.setStartTime(meetingRequest.getStartTime());
        savedMeeting.setEndTime(meetingRequest.getEndTime());
        savedMeeting.setOwner(owner);
        savedMeeting.setParticipant(requestor);
        savedMeeting.setLocalDate(meetingRequest.getLocalDate());
    }


    @Test
    void testRequestMeeting_Success() {
        when(employeeRepository.findByEmployeeId(eq("E123"))).thenReturn(java.util.Optional.of(owner));
        when(employeeRepository.findByEmployeeId(eq("E456"))).thenReturn(java.util.Optional.of(requestor));
        Meeting result = meetingService.requestMeeting("E456", meetingRequest);
        verify(employeeRepository, times(1)).findByEmployeeId("E123");
        verify(employeeRepository, times(1)).findByEmployeeId("E456");
    }


    @Test
    void testRequestMeeting_OwnerNotFound() {
        when(employeeRepository.findByEmployeeId(eq("E123"))).thenReturn(java.util.Optional.empty());

        OwnerNotFoundException exception = assertThrows(OwnerNotFoundException.class, () ->
                meetingService.requestMeeting("E456", meetingRequest)
        );

        assertEquals("Calendar owner not found", exception.getMessage());
        assertEquals(ErrorCodes.OWNER_NOT_FOUND, exception.getErrorCodes());
    }

    @Test
    void testRequestMeeting_EmployeeNotFound() {
        when(employeeRepository.findByEmployeeId(eq("E123"))).thenReturn(java.util.Optional.of(owner));
        when(employeeRepository.findByEmployeeId(eq("E456"))).thenReturn(java.util.Optional.empty());

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () ->
                meetingService.requestMeeting("E456", meetingRequest)
        );

        assertEquals("Requesting employee not found", exception.getMessage());
        assertEquals(ErrorCodes.EMPLOYEE_NOT_FOUND, exception.getErrorCodes());
    }


    @Test
    void testFindFreeTimeSlotsForEmployee_Success() {
        CommonFreeSlotRequestDto requestDto = new CommonFreeSlotRequestDto();
        requestDto.setEmployee1Id("E123");
        requestDto.setEmployee2Id("E456");
        requestDto.setRequestedDay(LocalDate.of(2024, 10, 18));

        Employee owner = new Employee();
        owner.setEmployeeId("E123");

        Employee requestor = new Employee();
        requestor.setEmployeeId("E456");

        Meeting savedMeeting = new Meeting();
        savedMeeting.setStartTime(LocalDateTime.of(2024, 10, 18, 9, 0));
        savedMeeting.setEndTime(LocalDateTime.of(2024, 10, 18, 10, 0));

        Meeting secondMeeting = new Meeting();
        secondMeeting.setStartTime(LocalDateTime.of(2024, 10, 18, 11, 0));
        secondMeeting.setEndTime(LocalDateTime.of(2024, 10, 18, 12, 0));

        when(employeeRepository.findByEmployeeId("E123")).thenReturn(Optional.of(owner));
        when(employeeRepository.findByEmployeeId("E456")).thenReturn(Optional.of(requestor));
        when(meetingRepository.findByParticipantAndDate(owner, LocalDate.of(2024, 10, 18)))
                .thenReturn(List.of(savedMeeting));
        when(meetingRepository.findByParticipantAndDate(requestor, LocalDate.of(2024, 10, 18)))
                .thenReturn(List.of(secondMeeting));

        BookMeetingDto result = meetingService.findFreeTimeSlotsForEmployee(requestDto);

        assertNotNull(result);
        assertFalse(result.getTimeSlots().isEmpty());
        assertEquals(20, result.getTimeSlots().size());
    }


    @Test
    void testFindFreeTimeSlotsForEmployee_SameEmployeeId() {
        CommonFreeSlotRequestDto requestDto = new CommonFreeSlotRequestDto();
        requestDto.setEmployee1Id("E123");
        requestDto.setEmployee2Id("E123");
        requestDto.setRequestedDay(LocalDate.of(2024, 10, 18));

        NotValidException exception = assertThrows(NotValidException.class,
                () -> meetingService.findFreeTimeSlotsForEmployee(requestDto));
        assertEquals("Employee1 and Employee2 should not equal", exception.getMessage());
    }

    @Test
    void testFindFreeTimeSlotsForEmployee_EmployeeNotFound() {
        CommonFreeSlotRequestDto requestDto = new CommonFreeSlotRequestDto();
        requestDto.setEmployee1Id("E123");
        requestDto.setEmployee2Id("E456");
        requestDto.setRequestedDay(LocalDate.of(2024, 10, 18));

        when(employeeRepository.findByEmployeeId("E123")).thenReturn(Optional.of(owner));
        when(employeeRepository.findByEmployeeId("E456")).thenReturn(Optional.empty());

        OwnerNotFoundException exception = assertThrows(OwnerNotFoundException.class,
                () -> meetingService.findFreeTimeSlotsForEmployee(requestDto));
        assertEquals("Calendar owner not found", exception.getMessage());
    }


    @Test
    void testFindParticipantsWithConflicts_Success() {
        ParticipantsRequest meetingRequest = new ParticipantsRequest();
        meetingRequest.setLocalDate(LocalDate.of(2024, 10, 18));
        meetingRequest.setStartTime(LocalDateTime.of(2024, 10, 18, 10, 0));
        meetingRequest.setEndTime(LocalDateTime.of(2024, 10, 18, 11, 0));

        meetingRequest.setParticipant(List.of(owner, requestor));

        Meeting existingMeeting = new Meeting();
        existingMeeting.setStartTime(LocalDateTime.of(2024, 10, 18, 9, 30));
        existingMeeting.setEndTime(LocalDateTime.of(2024, 10, 18, 10, 30));
        existingMeeting.setLocalDate(LocalDate.of(2024, 10, 18));
        existingMeeting.setParticipant(owner);

        when(employeeRepository.findByEmployeeId("E123")).thenReturn(Optional.of(owner));
        when(employeeRepository.findByEmployeeId("E456")).thenReturn(Optional.of(requestor));
        when(meetingRepository.findByParticipantAndDate(owner, meetingRequest.getLocalDate()))
                .thenReturn(List.of(existingMeeting));
        when(meetingRepository.findByParticipantAndDate(requestor, meetingRequest.getLocalDate()))
                .thenReturn(List.of());

        List<Employee> result = meetingService.findParticipantsWithConflicts(meetingRequest);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("E123", result.get(0).getEmployeeId());
    }

    @Test
    void testFindParticipantsWithConflicts_EmployeeNotFound() {
        ParticipantsRequest meetingRequest = new ParticipantsRequest();
        meetingRequest.setLocalDate(LocalDate.of(2024, 10, 18));
        meetingRequest.setStartTime(LocalDateTime.of(2024, 10, 18, 10, 0));
        meetingRequest.setEndTime(LocalDateTime.of(2024, 10, 18, 11, 0));

        meetingRequest.setParticipant(List.of(owner, requestor));

        when(employeeRepository.findByEmployeeId("E123")).thenReturn(Optional.of(owner));
        when(employeeRepository.findByEmployeeId("E456")).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class,
                () -> meetingService.findParticipantsWithConflicts(meetingRequest));
        assertEquals("Employee not found: E456", exception.getMessage());
    }

    @Test
    void testFindParticipantsWithConflicts_NoConflicts() {
        ParticipantsRequest meetingRequest = new ParticipantsRequest();
        meetingRequest.setLocalDate(LocalDate.of(2024, 10, 18));
        meetingRequest.setStartTime(LocalDateTime.of(2024, 10, 18, 10, 0));
        meetingRequest.setEndTime(LocalDateTime.of(2024, 10, 18, 11, 0));

        meetingRequest.setParticipant(List.of(owner, requestor));

        when(employeeRepository.findByEmployeeId("E123")).thenReturn(Optional.of(owner));
        when(employeeRepository.findByEmployeeId("E456")).thenReturn(Optional.of(requestor));

        Meeting existingMeeting = new Meeting();
        existingMeeting.setStartTime(LocalDateTime.of(2024, 10, 18, 11, 0));
        existingMeeting.setEndTime(LocalDateTime.of(2024, 10, 18, 12, 0));
        existingMeeting.setLocalDate(LocalDate.of(2024, 10, 18));
        existingMeeting.setParticipant(owner);

        when(meetingRepository.findByParticipantAndDate(owner, meetingRequest.getLocalDate()))
                .thenReturn(List.of(existingMeeting));
        when(meetingRepository.findByParticipantAndDate(requestor, meetingRequest.getLocalDate()))
                .thenReturn(List.of());

        List<Employee> result = meetingService.findParticipantsWithConflicts(meetingRequest);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testHasConflict_Success() {
        ParticipantsRequest meetingRequest = new ParticipantsRequest();
        meetingRequest.setStartTime(LocalDateTime.of(2024, 10, 18, 10, 0));
        meetingRequest.setEndTime(LocalDateTime.of(2024, 10, 18, 11, 0));

        Meeting existingMeeting = new Meeting();
        existingMeeting.setStartTime(LocalDateTime.of(2024, 10, 18, 9, 30));
        existingMeeting.setEndTime(LocalDateTime.of(2024, 10, 18, 10, 30));

        boolean result = meetingService.hasConflict(owner, meetingRequest);

        assertFalse(result);
    }

    @Test
    void testHasConflict_NoConflict() {
        ParticipantsRequest meetingRequest = new ParticipantsRequest();
        meetingRequest.setStartTime(LocalDateTime.of(2024, 10, 18, 12, 0));
        meetingRequest.setEndTime(LocalDateTime.of(2024, 10, 18, 13, 0));

        Meeting existingMeeting = new Meeting();
        existingMeeting.setStartTime(LocalDateTime.of(2024, 10, 18, 11, 0));
        existingMeeting.setEndTime(LocalDateTime.of(2024, 10, 18, 12, 0));

        when(meetingRepository.findByParticipantAndDate(owner, meetingRequest.getLocalDate()))
                .thenReturn(List.of(existingMeeting));

        boolean result = meetingService.hasConflict(owner, meetingRequest);

        assertFalse(result);
    }

}