package com.meshwarcoders.catalyst.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.List;

public class CreateLessonRequest {
    
    @NotBlank(message = "Subject is required")
    private String subject;
    
    private List<ScheduleRequest> schedules;

    // Inner class for schedule
    public static class ScheduleRequest {
        @NotNull(message = "Start time is required")
        private LocalDateTime startTime;
        
        @NotNull(message = "Duration is required")
        @Min(value = 15, message = "Duration must be at least 15 minutes")
        private Integer duration; // in minutes

        // Getters & Setters
        public LocalDateTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }
    }

    // Getters & Setters
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<ScheduleRequest> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<ScheduleRequest> schedules) {
        this.schedules = schedules;
    }
}