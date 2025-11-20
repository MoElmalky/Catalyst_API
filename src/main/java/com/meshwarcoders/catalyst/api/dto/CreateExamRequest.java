package com.meshwarcoders.catalyst.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateExamRequest {

    @NotBlank(message = "examName is required")
    private String examName;

    @NotNull(message = "maxGrade is required")
    private Integer maxGrade;

    // ISO-8601 datetime string, e.g. 2025-11-20T10:00:00
    private String examDateTime;

    private Integer durationMinutes;

    private List<ExamQuestionRequest> questions;

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public Integer getMaxGrade() {
        return maxGrade;
    }

    public void setMaxGrade(Integer maxGrade) {
        this.maxGrade = maxGrade;
    }

    public String getExamDateTime() {
        return examDateTime;
    }

    public void setExamDateTime(String examDateTime) {
        this.examDateTime = examDateTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public List<ExamQuestionRequest> getQuestions() {
        return questions;
    }

    public void setQuestions(List<ExamQuestionRequest> questions) {
        this.questions = questions;
    }
}