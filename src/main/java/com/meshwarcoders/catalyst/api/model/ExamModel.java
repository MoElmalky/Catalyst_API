package com.meshwarcoders.catalyst.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meshwarcoders.catalyst.api.dto.ExamSummaryDto;
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

    public void setLesson(LessonModel lesson) {
        this.lesson = lesson;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public void setMaxGrade(Integer maxGrade) {
        this.maxGrade = maxGrade;
    }

    public void setExamDateTime(LocalDateTime examDateTime) {
        this.examDateTime = examDateTime;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public LocalDateTime getExamDateTime() {
        return examDateTime;
    }

    public Long getId() {
        return id;
    }

    public String getExamName() {
        return examName;
    }

    public LessonModel getLesson() {
        return lesson;
    }

    public Integer getMaxGrade() {
        return maxGrade;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }
}
