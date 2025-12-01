package com.meshwarcoders.catalyst.api.model;

import com.meshwarcoders.catalyst.api.model.common.EnrollmentStatus;
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

    public Long getId() {
        return id;
    }

    public StudentModel getStudent() {
        return student;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public void setLesson(LessonModel lesson) {
        this.lesson = lesson;
    }

    public void setStudent(StudentModel student) {
        this.student = student;
    }
}
