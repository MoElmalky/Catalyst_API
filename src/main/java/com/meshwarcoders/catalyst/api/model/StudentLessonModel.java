package com.meshwarcoders.catalyst.api.model;

import jakarta.persistence.*;

@Entity(name = "student_lessons")
public class StudentLessonModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private LessonModel lesson;

    @ManyToOne
    private StudentModel student;

}
