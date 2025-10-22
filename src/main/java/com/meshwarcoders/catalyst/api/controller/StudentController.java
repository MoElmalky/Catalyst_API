package com.meshwarcoders.catalyst.api.controller;

import com.meshwarcoders.catalyst.api.dto.*;
import com.meshwarcoders.catalyst.api.model.StudentModel;
import com.meshwarcoders.catalyst.api.repository.StudentRepository;
import com.meshwarcoders.catalyst.api.security.JwtUtils;
import com.meshwarcoders.catalyst.api.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private StudentRepository studentRepository;

    // ================== SIGNUP ==================
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signUp(@Valid @RequestBody StudentSignUpRequest request) {
        try {
            StudentAuthResponse authResponse = studentService.signUp(request);
            ApiResponse response = new ApiResponse(true, "Student registered successfully!", authResponse);
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
            StudentAuthResponse authResponse = studentService.login(request);
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
            studentService.forgotPassword(request);
            ApiResponse response = new ApiResponse(true,
                    "Password reset instructions have been sent to your email!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ================== VERIFY RESET CODE ==================
    @PostMapping("/verify-reset-code")
    public ResponseEntity<ApiResponse> verifyResetCode(@Valid @RequestBody VerifyResetCodeRequest request) {
        try {
            String data = studentService.verifyResetCode(request);
            ApiResponse response = new ApiResponse(true, "Code verified successfully!", data);
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
            studentService.resetPassword(request);
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

            StudentModel student = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Student not found!"));

            StudentAuthResponse data = new StudentAuthResponse(
                    token,
                    student.getId(),
                    student.getFullName(),
                    student.getEmail(),
                    student.getBirthYear(),
                    student.getCurrentAcademicYear(),
                    student.getPhoneNumber(),
                    student.getBio()
            );

            return ResponseEntity.ok(new ApiResponse(true, "Profile fetched successfully!", data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== GET ALL STUDENTS ==================
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllStudents() {
        try {
            var students = studentRepository.findAll();
            return ResponseEntity.ok(
                    new ApiResponse(true, "Students fetched successfully!", students));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== GET STUDENT BY ID ==================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getStudentById(@PathVariable Long id) {
        try {
            var student = studentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Student not found!"));
            return ResponseEntity.ok(
                    new ApiResponse(true, "Student fetched successfully!", student));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== UPDATE PROFILE ==================
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody StudentSignUpRequest request) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Missing or invalid token!"));
            }

            String token = authHeader.substring(7);
            String email = jwtUtils.getEmailFromToken(token);

            StudentModel student = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Student not found!"));

            // Update fields
            student.setFullName(request.getFullName());
            student.setBirthYear(request.getBirthYear());
            student.setCurrentAcademicYear(request.getCurrentAcademicYear());
            student.setPhoneNumber(request.getPhoneNumber());
            student.setBio(request.getBio());

            student = studentRepository.save(student);

            StudentAuthResponse data = new StudentAuthResponse(
                    token,
                    student.getId(),
                    student.getFullName(),
                    student.getEmail(),
                    student.getBirthYear(),
                    student.getCurrentAcademicYear(),
                    student.getPhoneNumber(),
                    student.getBio()
            );

            return ResponseEntity.ok(new ApiResponse(true, "Profile updated successfully!", data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}