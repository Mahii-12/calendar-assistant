package com.calendar.assistant.dto;

import com.calendar.assistant.entity.Meeting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookMeetingDto {

    private List<TimeSlot> timeSlots;
    private Meeting meeting;
}
