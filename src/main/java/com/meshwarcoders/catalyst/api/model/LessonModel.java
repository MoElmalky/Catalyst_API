package com.meshwarcoders.catalyst.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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

    @JsonIgnore
    @OneToMany(mappedBy = "lesson")
    private List<StudentLessonModel> studentLessons = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "lesson")
    private List<LessonScheduleModel> lessonSchedules = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "lesson")
    private List<ExamModel> exams = new ArrayList<>();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public TeacherModel getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherModel teacher) {
        this.teacher = teacher;
    }

    public List<StudentLessonModel> getStudentLessons() {
        return studentLessons;
    }

    public void setStudentLessons(List<StudentLessonModel> studentLessons) {
        this.studentLessons = studentLessons;
    }

    public List<LessonScheduleModel> getLessonSchedules() {
        return lessonSchedules;
    }

    public void setLessonSchedules(List<LessonScheduleModel> lessonSchedules) {
        this.lessonSchedules = lessonSchedules;
    }

    public List<ExamModel> getExams() {
        return exams;
    }

    public void setExams(List<ExamModel> exams) {
        this.exams = exams;
    }
}
