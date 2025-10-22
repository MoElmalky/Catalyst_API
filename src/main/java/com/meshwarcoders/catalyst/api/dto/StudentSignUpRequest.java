package com.meshwarcoders.catalyst.api.dto;

import com.meshwarcoders.catalyst.api.model.StudentModel;
import jakarta.validation.constraints.*;

// ================== STUDENT SIGN UP REQUEST ==================
public class StudentSignUpRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20, message = "Password must be between 6-20 characters")
    private String password;

    @NotNull(message = "Birth year is required")
    @Min(value = 1950, message = "Birth year must be after 1950")
    @Max(value = 2020, message = "Birth year must be before 2020")
    private Integer birthYear;

    @NotNull(message = "Academic year is required")
    private StudentModel.AcademicYear currentAcademicYear;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number should be valid")
    private String phoneNumber;

    @Size(max = 1000, message = "Bio cannot exceed 1000 characters")
    private String bio;

    // Getters and Setters
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