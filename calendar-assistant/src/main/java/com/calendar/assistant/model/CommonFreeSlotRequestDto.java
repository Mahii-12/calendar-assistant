package com.calendar.assistant.model;

import com.calendar.assistant.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonFreeSlotRequestDto {
    private String employee1Id;
    private String employee2Id;
    private LocalDate requestedDay;

}
