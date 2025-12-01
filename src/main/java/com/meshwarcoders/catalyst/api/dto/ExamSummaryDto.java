package com.meshwarcoders.catalyst.api.dto;

public class ExamSummaryDto {
    private Long id;
    private Long lessonId;
    private String examName;
    private Integer maxGrade;
    private String examDateTime;
    private Integer durationMinutes;

    public ExamSummaryDto(Long id, Long lessonId, String examName, Integer maxGrade,
                          String examDateTime, Integer durationMinutes) {
        this.id = id;
        this.lessonId = lessonId;
        this.examName = examName;
        this.maxGrade = maxGrade;
        this.examDateTime = examDateTime;
        this.durationMinutes = durationMinutes;
    }

    public Long getId() {
        return id;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public String getExamName() {
        return examName;
    }

    public Integer getMaxGrade() {
        return maxGrade;
    }

    public String getExamDateTime() {
        return examDateTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }
}