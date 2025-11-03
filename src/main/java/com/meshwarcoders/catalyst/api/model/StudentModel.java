package com.meshwarcoders.catalyst.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "students")
public class StudentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true)
    private String email;

    private String password;

    private Integer birthYear;

    @Enumerated(EnumType.STRING)
    private AcademicYear currentAcademicYear;

    private String phoneNumber;

    @Column(length = 1000)
    private String bio;

    private String resetPasswordToken;

    private LocalDateTime resetPasswordTokenExpiry;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "student")
    private List<StudentLessonModel> studentLessons = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "student")
    private List<StudentExamModel> studentExams = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Enum for Academic Year
    public enum AcademicYear {
        FIRST_YEAR,
        SECOND_YEAR,
        THIRD_YEAR,
        FOURTH_YEAR,
        FIFTH_YEAR,
        SIXTH_YEAR
    }

    // Getters and Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public AcademicYear getCurrentAcademicYear() {
        return currentAcademicYear;
    }

    public void setCurrentAcademicYear(AcademicYear currentAcademicYear) {
        this.currentAcademicYear = currentAcademicYear;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public LocalDateTime getResetPasswordTokenExpiry() {
        return resetPasswordTokenExpiry;
    }

    public void setResetPasswordTokenExpiry(LocalDateTime resetPasswordTokenExpiry) {
        this.resetPasswordTokenExpiry = resetPasswordTokenExpiry;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<StudentLessonModel> getStudentLessons() {
        return studentLessons;
    }

    public void setStudentLessons(List<StudentLessonModel> studentLessons) {
        this.studentLessons = studentLessons;
    }

    public List<StudentExamModel> getStudentExams() {
        return studentExams;
    }

    public void setStudentExams(List<StudentExamModel> studentExams) {
        this.studentExams = studentExams;
    }
}