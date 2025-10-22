package com.meshwarcoders.catalyst.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class JoinClassRequest {
    @NotNull(message = "Lesson ID is required")
    private Long lessonId;

    @Size(max = 500, message = "Message cannot exceed 500 characters")
    private String message;

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
