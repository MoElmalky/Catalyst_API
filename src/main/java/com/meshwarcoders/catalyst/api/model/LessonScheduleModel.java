package com.meshwarcoders.catalyst.api.model;

import com.meshwarcoders.catalyst.api.dto.LessonScheduleDto;
import com.meshwarcoders.catalyst.api.model.common.WeekDay;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity(name = "lesson_schedules")
public class LessonScheduleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private LessonModel lesson;

    private LocalTime startTime;

    private WeekDay day;

    private Integer duration;

    public Integer getDuration() {
        return duration;
    }

    public WeekDay getDay() {
        return day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setLesson(LessonModel lesson) {
        this.lesson = lesson;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setDay(WeekDay day) {
        this.day = day;
    }

    @Override
    public String toString(){
        return "Day: "+day+"\nTime: "+startTime.toString()+"\nDuration: "+duration;
    }
}
