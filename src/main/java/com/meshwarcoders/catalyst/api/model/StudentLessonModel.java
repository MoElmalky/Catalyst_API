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

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LessonModel getLesson() {
        return lesson;
    }

    public void setLesson(LessonModel lesson) {
        this.lesson = lesson;
    }

    public StudentModel getStudent() {
        return student;
    }

    public void setStudent(StudentModel student) {
        this.student = student;
    }
}