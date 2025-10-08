package com.meshwarcoders.catalyst.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity(name = "lessons")
public class LessonModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "subject cannot be null")
    @NotBlank(message = "subject cannot be blank")
    private String subject;

    @ManyToOne
    private TeacherModel teacher;
}
