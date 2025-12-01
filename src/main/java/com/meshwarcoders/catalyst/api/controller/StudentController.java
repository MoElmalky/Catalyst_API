package com.meshwarcoders.catalyst.api.controller;

import com.meshwarcoders.catalyst.api.dto.*;
import com.meshwarcoders.catalyst.api.exception.NotFoundException;
import com.meshwarcoders.catalyst.api.exception.UnauthorizedException;
import com.meshwarcoders.catalyst.api.model.StudentModel;
import com.meshwarcoders.catalyst.api.repository.StudentRepository;
import com.meshwarcoders.catalyst.api.service.ExamService;
import com.meshwarcoders.catalyst.api.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ExamService examService;

    @Autowired
    private StudentRepository studentRepository;

    // ================== SIGNUP ==================
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        AuthResponse authResponse = studentService.signUp(request);
        ApiResponse response = new ApiResponse(true, "Student registered successfully!", authResponse);
        return ResponseEntity.ok(response);
    }

    // ================== LOGIN ==================
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = studentService.login(request);
        ApiResponse response = new ApiResponse(true, "Login successful!", authResponse);
        return ResponseEntity.ok(response);
    }

    // ================== CONFIRM EMAIL ==================
    @PostMapping("/confirm-email")
    public ResponseEntity<ApiResponse> confirmEmail(@Valid @RequestBody ConfirmEmailRequest request) {
        studentService.confirmEmail(request);
        ApiResponse response = new ApiResponse(true, "Email confirmed successfully!");
        return ResponseEntity.ok(response);
    }

    // Allow confirmation via GET link from email so backend handles everything
    @GetMapping("/confirm-email")
    public ResponseEntity<String> confirmEmailViaLink(@RequestParam("email") String email,
                                                      @RequestParam("code") String code) {
        ConfirmEmailRequest request = new ConfirmEmailRequest();
        request.setEmail(email);
        request.setCode(code);
        studentService.confirmEmail(request);
        return ResponseEntity.ok("Email confirmed successfully. You can close this page and return to the app.");
    }

    // ================== PROFILE (GET) ==================
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(Authentication authentication,
                                                 @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Missing or invalid token!");
        }

        String email = authentication.getName();
        StudentModel student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Student not found!"));

        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        AuthResponse data = new AuthResponse(
                token,
                student.getId(),
                student.getFullName(),
                student.getEmail());

        return ResponseEntity.ok(new ApiResponse(true, "Profile fetched successfully!", data));
    }

    // ================== JOIN REQUEST (STUDENT) ==================
    @PostMapping("/lessons/{lessonId}/join-request")
    public ResponseEntity<ApiResponse> createJoinRequest(@PathVariable Long lessonId,
                                                         Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Missing or invalid token!");
        }

        String email = authentication.getName();
        JoinRequestDto dto = studentService.createJoinRequest(lessonId, email);

        return ResponseEntity.ok(new ApiResponse(true,
                "Join request created successfully!", dto));
    }

    // ================== EXAMS (STUDENT) ==================
    @GetMapping("/lessons/{lessonId}/exams")
    public ResponseEntity<ApiResponse> getExamsForLesson(@PathVariable Long lessonId,
                                                         Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Missing or invalid token!");
        }

        String email = authentication.getName();
        var exams = examService.getExamsForLessonAsStudent(email, lessonId);
        return ResponseEntity.ok(new ApiResponse(true,
                "Exams fetched successfully!", exams));
    }
}
