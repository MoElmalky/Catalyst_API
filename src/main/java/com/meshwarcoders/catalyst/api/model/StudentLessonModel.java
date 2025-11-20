package com.meshwarcoders.catalyst.api.model;

import jakarta.persistence.*;

@Entity(name = "student_lessons")
public class StudentLessonModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private LessonModel lesson;

    @ManyToOne(optional = false)
    private StudentModel student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.PENDING;

}
