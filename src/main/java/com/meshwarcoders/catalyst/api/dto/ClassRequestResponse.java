package com.meshwarcoders.catalyst.api.dto;

import com.meshwarcoders.catalyst.api.model.ClassRequestModel;
import java.time.LocalDateTime;

public class ClassRequestResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Long lessonId;
    private String lessonSubject;
    private ClassRequestModel.RequestStatus status;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;

    public ClassRequestResponse(ClassRequestModel request) {
        this.id = request.getId();
        this.studentId = request.getStudent().getId();
        this.studentName = request.getStudent().getFullName();
        this.studentEmail = request.getStudent().getEmail();
        this.lessonId = request.getLesson().getId();
        this.lessonSubject = request.getLesson().getSubject();
        this.status = request.getStatus();
        this.message = request.getMessage();
        this.createdAt = request.getCreatedAt();
        this.respondedAt = request.getRespondedAt();
    }

    // Getters and Setters
    // ...
}
