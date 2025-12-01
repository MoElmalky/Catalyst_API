package com.meshwarcoders.catalyst.api.dto;

import java.time.LocalDateTime;
import java.util.List;

public class HomeDashboardResponse {
    private List<TeacherInfo> teachers;
    private List<LessonInfo> lessons;
    private List<NotificationInfo> notifications;

    public HomeDashboardResponse(List<TeacherInfo> teachers, List<LessonInfo> lessons, List<NotificationInfo> notifications) {
        this.teachers = teachers;
        this.lessons = lessons;
        this.notifications = notifications;
    }

    // ===== Teacher Info =====
    public static class TeacherInfo {
        private Long id;
        private String fullName;
        private String email;
        private Integer lessonsCount;

        public TeacherInfo(Long id, String fullName, String email, Integer lessonsCount) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.lessonsCount = lessonsCount;
        }

        // Getters & Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Integer getLessonsCount() { return lessonsCount; }
        public void setLessonsCount(Integer lessonsCount) { this.lessonsCount = lessonsCount; }
    }

    // ===== Lesson Info =====
    public static class LessonInfo {
        private Long id;
        private String subject;
        private String teacherName;
        private Integer studentsCount;

        public LessonInfo(Long id, String subject, String teacherName, Integer studentsCount) {
            this.id = id;
            this.subject = subject;
            this.teacherName = teacherName;
            this.studentsCount = studentsCount;
        }

        // Getters & Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getTeacherName() { return teacherName; }
        public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
        public Integer getStudentsCount() { return studentsCount; }
        public void setStudentsCount(Integer studentsCount) { this.studentsCount = studentsCount; }
    }

    // ===== Notification Info =====
    public static class NotificationInfo {
        private String type; // NEW_REQUEST, APPROVED, REJECTED, NEW_STUDENT
        private String message;
        private LocalDateTime timestamp;
        private Long relatedId; // request ID or student ID

        public NotificationInfo(String type, String message, LocalDateTime timestamp, Long relatedId) {
            this.type = type;
            this.message = message;
            this.timestamp = timestamp;
            this.relatedId = relatedId;
        }

        // Getters & Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public Long getRelatedId() { return relatedId; }
        public void setRelatedId(Long relatedId) { this.relatedId = relatedId; }
    }

    // Main Getters & Setters
    public List<TeacherInfo> getTeachers() { return teachers; }
    public void setTeachers(List<TeacherInfo> teachers) { this.teachers = teachers; }
    public List<LessonInfo> getLessons() { return lessons; }
    public void setLessons(List<LessonInfo> lessons) { this.lessons = lessons; }
    public List<NotificationInfo> getNotifications() { return notifications; }
    public void setNotifications(List<NotificationInfo> notifications) { this.notifications = notifications; }
}