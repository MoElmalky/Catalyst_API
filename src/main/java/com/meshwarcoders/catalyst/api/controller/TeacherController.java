package com.meshwarcoders.catalyst.api.controller;

import com.meshwarcoders.catalyst.api.dto.*;
import com.meshwarcoders.catalyst.api.model.TeacherModel;
import com.meshwarcoders.catalyst.api.repository.TeacherRepository;
import com.meshwarcoders.catalyst.api.security.JwtUtils;
import com.meshwarcoders.catalyst.api.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "*")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private TeacherRepository teacherRepository;

    // ================== SIGNUP ==================
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        try {
            AuthResponse authResponse = teacherService.signUp(request);
            ApiResponse response = new ApiResponse(true, "Teacher registered successfully!", authResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ================== LOGIN ==================
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse authResponse = teacherService.login(request);
            ApiResponse response = new ApiResponse(true, "Login successful!", authResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // ================== FORGOT PASSWORD ==================
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            teacherService.forgotPassword(request);
            ApiResponse response = new ApiResponse(true,
                    "Password reset instructions have been sent to your email!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ================== RESET PASSWORD ==================
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            teacherService.resetPassword(request);
            ApiResponse response = new ApiResponse(true, "Password reset successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ================== PROFILE (GET) ==================
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Missing or invalid token!"));
            }

            String token = authHeader.substring(7);
            String email = jwtUtils.getEmailFromToken(token);

            TeacherModel teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Teacher not found!"));

            AuthResponse data = new AuthResponse(
                    token,
                    teacher.getId(),
                    teacher.getFullName(),
                    teacher.getEmail());

            return ResponseEntity.ok(new ApiResponse(true, "Profile fetched successfully!", data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== GET ALL TEACHERS ==================
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllTeachers() {
        try {
            var teachers = teacherRepository.findAll();
            return ResponseEntity.ok(
                    new ApiResponse(true, "Teachers fetched successfully!", teachers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== GET TEACHER BY ID ==================

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getTeacherById(@PathVariable Long id) {
        try {
            var teacher = teacherRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Teacher not found!"));
            return ResponseEntity.ok(
                    new ApiResponse(true, "Teacher fetched successfully!", teacher));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

}
