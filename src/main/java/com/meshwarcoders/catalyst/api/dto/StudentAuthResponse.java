

// ================== STUDENT AUTH RESPONSE ==================
package com.meshwarcoders.catalyst.api.dto;

import com.meshwarcoders.catalyst.api.model.StudentModel;

public class StudentAuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String fullName;
    private String email;
    private Integer birthYear;
    private StudentModel.AcademicYear currentAcademicYear;
    private String phoneNumber;
    private String bio;

    public StudentAuthResponse(String token, Long id, String fullName, String email,
                               Integer birthYear, StudentModel.AcademicYear currentAcademicYear,
                               String phoneNumber, String bio) {
        this.token = token;
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.birthYear = birthYear;
        this.currentAcademicYear = currentAcademicYear;
        this.phoneNumber = phoneNumber;
        this.bio = bio;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public StudentModel.AcademicYear getCurrentAcademicYear() {
        return currentAcademicYear;
    }

    public void setCurrentAcademicYear(StudentModel.AcademicYear currentAcademicYear) {
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
}