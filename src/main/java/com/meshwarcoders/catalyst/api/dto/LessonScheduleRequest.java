package com.meshwarcoders.catalyst.api.dto;

import com.meshwarcoders.catalyst.api.model.common.WeekDay;

public class LessonScheduleRequest {

    private Integer duration;

    private String startTime;

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
