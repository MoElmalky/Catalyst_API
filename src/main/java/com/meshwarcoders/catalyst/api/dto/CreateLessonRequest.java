package com.meshwarcoders.catalyst.api.dto;

import com.meshwarcoders.catalyst.api.model.common.WeekDay;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalTime;
import java.util.List;

public class CreateLessonRequest {

    @NotBlank(message = "subject is required")
    private String subject;

    @Valid
    @NotEmpty(message = "lessonSchedules is required")
    private List<LessonScheduleRequest> lessonSchedules;


    public String getSubject() {
        return subject;
    }

    public List<LessonScheduleRequest> getLessonSchedules() {
        return lessonSchedules;
    }
}
