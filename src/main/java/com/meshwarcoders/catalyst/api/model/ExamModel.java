package com.meshwarcoders.catalyst.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "exams")
public class ExamModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String examName;

    @ManyToOne(optional = false)
    private LessonModel lesson;

    private Integer maxGrade;

    private LocalDateTime examDateTime;

    private Integer durationMinutes;

    @JsonIgnore
    @OneToMany(mappedBy = "exam")
    private List<StudentExamModel> studentExams = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "exam")
    private List<ExamQuestionModel> questions = new ArrayList<>();
}
