package com.meshwarcoders.catalyst.api.model;

import com.meshwarcoders.catalyst.api.model.common.WeekDay;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity(name = "lesson_schedules")
public class LessonScheduleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "lesson_id")
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
}
