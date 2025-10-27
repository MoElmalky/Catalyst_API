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

    public Long getId() { return id; }
public void setId(Long id) { this.id = id; }

public Long getStudentId() { return studentId; }
public void setStudentId(Long studentId) { this.studentId = studentId; }

public String getStudentName() { return studentName; }
public void setStudentName(String studentName) { this.studentName = studentName; }

public String getStudentEmail() { return studentEmail; }
public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

public Long getLessonId() { return lessonId; }
public void setLessonId(Long lessonId) { this.lessonId = lessonId; }

public String getLessonSubject() { return lessonSubject; }
public void setLessonSubject(String lessonSubject) { this.lessonSubject = lessonSubject; }

public ClassRequestModel.RequestStatus getStatus() { return status; }
public void setStatus(ClassRequestModel.RequestStatus status) { this.status = status; }

public String getMessage() { return message; }
public void setMessage(String message) { this.message = message; }

public LocalDateTime getCreatedAt() { return createdAt; }
public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

public LocalDateTime getRespondedAt() { return respondedAt; }
public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }

}
