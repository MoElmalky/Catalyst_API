package com.meshwarcoders.catalyst.api.dto;

import com.meshwarcoders.catalyst.api.model.LessonModel;
import com.meshwarcoders.catalyst.api.model.LessonScheduleModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class LessonResponseDTO {
    private Long id;
    private String subject;
    private TeacherInfoDTO teacher;
    private Integer studentsCount;
    private List<ScheduleInfoDTO> schedules;

    // Constructor from LessonModel
    public LessonResponseDTO(LessonModel lesson, Integer studentsCount) {
        this.id = lesson.getId();
        this.subject = lesson.getSubject();
        this.teacher = new TeacherInfoDTO(
                lesson.getTeacher().getId(),
                lesson.getTeacher().getFullName()
        );
        this.studentsCount = studentsCount;
        this.schedules = lesson.getLessonSchedules().stream()
                .map(schedule -> new ScheduleInfoDTO(
                        schedule.getId(),
                        schedule.getStartTime(),
                        schedule.getDuration()
                ))
                .collect(Collectors.toList());
    }

    // ===== Inner Classes =====
    
    public static class TeacherInfoDTO {
        private Long id;
        private String fullName;

        public TeacherInfoDTO(Long id, String fullName) {
            this.id = id;
            this.fullName = fullName;
        }

        // Getters & Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
    }

    public static class ScheduleInfoDTO {
        private Long id;
        private LocalDateTime startTime;
        private Integer duration; // in minutes

        public ScheduleInfoDTO(Long id, LocalDateTime startTime, Integer duration) {
            this.id = id;
            this.startTime = startTime;
            this.duration = duration;
        }

        // Getters & Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }
    }

    // ===== Main Getters & Setters =====
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public TeacherInfoDTO getTeacher() { return teacher; }
    public void setTeacher(TeacherInfoDTO teacher) { this.teacher = teacher; }
    
    public Integer getStudentsCount() { return studentsCount; }
    public void setStudentsCount(Integer studentsCount) { this.studentsCount = studentsCount; }
    
    public List<ScheduleInfoDTO> getSchedules() { return schedules; }
    public void setSchedules(List<ScheduleInfoDTO> schedules) { this.schedules = schedules; }
}