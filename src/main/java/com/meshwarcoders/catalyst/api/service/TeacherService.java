package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.dto.*;
import com.meshwarcoders.catalyst.api.model.TeacherModel;
import com.meshwarcoders.catalyst.api.repository.TeacherRepository;
import com.meshwarcoders.catalyst.api.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    @Transactional
    public AuthResponse signUp(SignUpRequest request) {
        // Check if email already exists
        if (teacherRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }

        // Create new teacher
        TeacherModel teacher = new TeacherModel();
        teacher.setFullName(request.getFullName());
        teacher.setEmail(request.getEmail());
        teacher.setPassword(passwordEncoder.encode(request.getPassword()));

        teacher = teacherRepository.save(teacher);

        // Generate JWT token
        String token = jwtUtils.generateToken(teacher.getEmail());

        return new AuthResponse(token, teacher.getId(), teacher.getFullName(), teacher.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        // Find teacher by email
        TeacherModel teacher = teacherRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email!"));

        // Check password
        if (!passwordEncoder.matches(request.getPassword(), teacher.getPassword())) {
            throw new RuntimeException("Invalid password!");
        }

        // Generate JWT token
        String token = jwtUtils.generateToken(teacher.getEmail());

        return new AuthResponse(token, teacher.getId(), teacher.getFullName(), teacher.getEmail());
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) throws IOException {
        // Find teacher by email
        TeacherModel teacher = teacherRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No account found with this email!"));

        // Generate reset token
        String resetCode = UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        teacher.setResetPasswordToken(resetCode);
        teacher.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(5));

        emailService.sendEmail(teacher.getEmail(),resetCode);

        teacherRepository.save(teacher);


    }

    @Transactional(readOnly = true)
    public String verifyResetCode(VerifyResetCodeRequest request){
        TeacherModel teacher = teacherRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No account found with this email!"));

        if(!teacher.getResetPasswordToken().equals(request.getCode())){
            throw new RuntimeException("Invalid reset token!");
        }

        if(teacher.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Reset token has expired!");
        }

        return jwtUtils.generateResetToken(request.getEmail());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        String email = jwtUtils.validateResetToken(request.getResetToken());

        if (email == null) {
            throw new RuntimeException("Invalid reset token!");
        }

        TeacherModel teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found!"));

        // Update password
        teacher.setPassword(passwordEncoder.encode(request.getNewPassword()));
        teacher.setResetPasswordToken(null);
        teacher.setResetPasswordTokenExpiry(null);

        teacherRepository.save(teacher);
    }
}