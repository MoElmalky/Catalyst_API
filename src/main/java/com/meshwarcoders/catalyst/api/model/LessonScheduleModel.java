package com.meshwarcoders.catalyst.api.model;

import com.meshwarcoders.catalyst.api.model.common.WeekDay;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity(name = "lesson_schedules")
@Getter @Setter
public class LessonScheduleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    private LessonModel lesson;

    private LocalTime startTime;

    private WeekDay day;

    private Integer duration;
}
