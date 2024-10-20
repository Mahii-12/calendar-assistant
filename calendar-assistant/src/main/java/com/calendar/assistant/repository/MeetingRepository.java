package com.calendar.assistant.repository;

import com.calendar.assistant.entity.Employee;
import com.calendar.assistant.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    @Query("SELECT m FROM Meeting m WHERE m.owner = :owner AND m.localDate = :localDate")
    List<Meeting> findByParticipantAndDate(@Param("owner") Employee owner, @Param("localDate") LocalDate localDate);

    List<Meeting> findByParticipant(Employee participant);
}
