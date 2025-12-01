package com.meshwarcoders.catalyst.api.dto;

import com.meshwarcoders.catalyst.api.model.common.WeekDay;

public class LessonScheduleDto {
    private String startTime;
    private WeekDay day;
    private Integer duration;

    public LessonScheduleDto(String startTime, WeekDay day, Integer duration){
        this.day = day;
        this.startTime = startTime;
        this.duration = duration;
    }

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
