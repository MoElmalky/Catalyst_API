package com.meshwarcoders.catalyst.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

@Entity(name = "lesson_schedules")
public class LessonScheduleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private LessonModel lesson;

    @NotNull(message = "day cannot be null")
    @NotBlank(message = "day cannot be blank")
    private String day;

    private LocalTime startTime;

    private Integer duration;
}
