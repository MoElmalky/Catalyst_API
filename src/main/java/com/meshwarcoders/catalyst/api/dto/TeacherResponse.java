package com.meshwarcoders.catalyst.api.dto;

import java.time.LocalDateTime;

public class TeacherResponse {
    private Long id;
    private String fullName;
    private String email;
    private boolean emailConfirmed;
    private LocalDateTime createdAt;

    public TeacherResponse(Long id, String fullName, String email, boolean emailConfirmed, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.emailConfirmed = emailConfirmed;
        this.createdAt = createdAt;
    }

    public TeacherResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}