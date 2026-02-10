package com.meshwarcoders.catalyst.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "lessons")
@Getter @Setter
public class LessonModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private String subject;

    @ManyToOne
    private TeacherModel teacher;


    @OneToMany(mappedBy = "lesson")
    private List<StudentLessonModel> studentLessons = new ArrayList<>();


    @OneToMany(mappedBy = "lesson")
    private List<LessonScheduleModel> lessonSchedules = new ArrayList<>();


    @OneToMany(mappedBy = "lesson")
    private List<ExamModel> exams = new ArrayList<>();
}
