package com.meshwarcoders.catalyst.api.dto;

import com.meshwarcoders.catalyst.api.model.common.WeekDay;
import jakarta.validation.constraints.NotNull;

public class LessonScheduleRequest {

    @NotNull(message = "duration is required")
    private Integer duration;

    @NotNull(message = "startTime is required")
    private String startTime;

    @NotNull(message = "day is required")
    private WeekDay day;

    public Integer getDuration() {
        return duration;
    }

    public String getStartTime() {
        return startTime;
    }

    public WeekDay getDay() {
        return day;
    }
}
