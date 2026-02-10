package com.meshwarcoders.catalyst.api.model;

import com.meshwarcoders.catalyst.api.model.common.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "student_lessons")
@Getter @Setter
public class StudentLessonModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(optional = false)
    private LessonModel lesson;

    @ManyToOne(optional = false)
    private StudentModel student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.PENDING;
}
