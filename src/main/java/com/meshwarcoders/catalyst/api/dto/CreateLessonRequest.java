package com.meshwarcoders.catalyst.api.dto;

import com.meshwarcoders.catalyst.api.model.common.WeekDay;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalTime;
import java.util.List;

public class CreateLessonRequest {

    @NotBlank(message = "subject is required")
    private String subject;

    private List<LessonScheduleRequest> scheduleRequests;


    public String getSubject() {
        return subject;
    }

    public List<LessonScheduleRequest> getLessonSchedules() {
        return scheduleRequests;
    }
}
