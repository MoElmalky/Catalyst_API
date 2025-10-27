package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.dto.*;
import com.meshwarcoders.catalyst.api.model.StudentModel;
import com.meshwarcoders.catalyst.api.repository.StudentRepository;
import com.meshwarcoders.catalyst.api.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Transactional
    public StudentAuthResponse signUp(StudentSignUpRequest request) {
        // Verify email is real
        if (!emailVerificationService.isEmailValid(request.getEmail())) {
            throw new RuntimeException("Invalid or disposable email address. Please use a real email!");
        }

        // Check if email already exists
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }

        // Validate birth year
        int currentYear = LocalDateTime.now().getYear();
        if (request.getBirthYear() > currentYear - 10) {
            throw new RuntimeException("Student must be at least 10 years old!");
        }

        // Create new student
        StudentModel student = new StudentModel();
        student.setFullName(request.getFullName());
        student.setEmail(request.getEmail());
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setBirthYear(request.getBirthYear());
        student.setCurrentAcademicYear(request.getCurrentAcademicYear());
        student.setPhoneNumber(request.getPhoneNumber());
        student.setBio(request.getBio());

        student = studentRepository.save(student);

        // Generate JWT token
        String token = jwtUtils.generateToken(student.getEmail(), "STUDENT");


        return new StudentAuthResponse(
                token,
                student.getId(),
                student.getFullName(),
                student.getEmail(),
                student.getBirthYear(),
                student.getCurrentAcademicYear(),
                student.getPhoneNumber(),
                student.getBio()
        );
    }

    public StudentAuthResponse login(LoginRequest request) {
        // Find student by email
        StudentModel student = studentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email!"));

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            throw new RuntimeException("Invalid password!");
        }

        // Generate JWT token
        String token = jwtUtils.generateToken(student.getEmail(), "STUDENT");

        return new StudentAuthResponse(
                token,
                student.getId(),
                student.getFullName(),
                student.getEmail(),
                student.getBirthYear(),
                student.getCurrentAcademicYear(),
                student.getPhoneNumber(),
                student.getBio()
        );
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) throws IOException {
        // Find student by email
        StudentModel student = studentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No account found with this email!"));

        // Generate reset code
        String resetCode = UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        student.setResetPasswordToken(resetCode);
        student.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(5));

        emailService.sendEmail(student.getEmail(), resetCode);

        studentRepository.save(student);
    }

    @Transactional(readOnly = true)
    public String verifyResetCode(VerifyResetCodeRequest request) {
        StudentModel student = studentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No account found with this email!"));

        if (!student.getResetPasswordToken().equals(request.getCode())) {
            throw new RuntimeException("Invalid reset code!");
        }

        if (student.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset code has expired!");
        }

        return jwtUtils.generateResetToken(request.getEmail());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String email = jwtUtils.validateResetToken(request.getResetToken());

        if (email == null) {
            throw new RuntimeException("Invalid reset token!");
        }

        StudentModel student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found!"));

        // Update password
        student.setPassword(passwordEncoder.encode(request.getNewPassword()));
        student.setResetPasswordToken(null);
        student.setResetPasswordTokenExpiry(null);

        studentRepository.save(student);
    }
}