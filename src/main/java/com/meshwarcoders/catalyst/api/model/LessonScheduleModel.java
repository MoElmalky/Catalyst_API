package com.meshwarcoders.catalyst.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity(name = "lesson_schedules")
public class LessonScheduleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private LessonModel lesson;

    private LocalDateTime startTime;

    private Integer duration;
}
