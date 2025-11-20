package com.meshwarcoders.catalyst.api.dto;

import com.meshwarcoders.catalyst.api.model.EnrollmentStatus;

public class JoinRequestDto {
    private Long id;
    private Long lessonId;
    private StudentSummaryDto student;
    private EnrollmentStatus status;

    public JoinRequestDto(Long id, Long lessonId, StudentSummaryDto student, EnrollmentStatus status) {
        this.id = id;
        this.lessonId = lessonId;
        this.student = student;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public StudentSummaryDto getStudent() {
        return student;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }
}