package com.meshwarcoders.catalyst.api.controller;

import com.meshwarcoders.catalyst.api.dto.*;
import com.meshwarcoders.catalyst.api.exception.NotFoundException;
import com.meshwarcoders.catalyst.api.exception.UnauthorizedException;
import com.meshwarcoders.catalyst.api.model.TeacherModel;
import com.meshwarcoders.catalyst.api.repository.TeacherRepository;
import com.meshwarcoders.catalyst.api.service.ExamService;
import com.meshwarcoders.catalyst.api.service.LessonService;
import com.meshwarcoders.catalyst.api.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = "*")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private ExamService examService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private TeacherRepository teacherRepository;

    // ================== SIGNUP ==================
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        AuthResponse authResponse = teacherService.signUp(request);
        ApiResponse response = new ApiResponse(true, "Teacher registered successfully!", authResponse);
        return ResponseEntity.ok(response);
    }

    // ================== LOGIN ==================
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = teacherService.login(request);
        ApiResponse response = new ApiResponse(true, "Login successful!", authResponse);
        return ResponseEntity.ok(response);
    }

    // ================== FORGOT PASSWORD ==================
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        teacherService.forgotPassword(request);
        ApiResponse response = new ApiResponse(true,
                "Password reset instructions have been sent to your email!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-reset-code")
    public ResponseEntity<ApiResponse> verifyResetCode(@Valid @RequestBody VerifyResetCodeRequest request){
        String data = teacherService.verifyResetCode(request);
        ApiResponse response = new ApiResponse(true, "Code verified successfully!", data);
        return ResponseEntity.ok(response);
    }

    // ================== RESET PASSWORD ==================
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        teacherService.resetPassword(request);
        ApiResponse response = new ApiResponse(true, "Password reset successfully!");
        return ResponseEntity.ok(response);
    }

    // ================== CONFIRM EMAIL ==================
    @PostMapping("/confirm-email")
    public ResponseEntity<ApiResponse> confirmEmail(@Valid @RequestBody ConfirmEmailRequest request) {
        teacherService.confirmEmail(request);
        ApiResponse response = new ApiResponse(true, "Email confirmed successfully!");
        return ResponseEntity.ok(response);
    }

    // New: allow confirmation via GET link from email so backend handles everything
    @GetMapping("/confirm-email")
    public ResponseEntity<String> confirmEmailViaLink(@RequestParam("email") String email,
                                                      @RequestParam("code") String code) {
        ConfirmEmailRequest request = new ConfirmEmailRequest();
        request.setEmail(email);
        request.setCode(code);
        teacherService.confirmEmail(request);
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
        TeacherModel teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        AuthResponse data = new AuthResponse(
                token,
                teacher.getId(),
                teacher.getFullName(),
                teacher.getEmail());

        return ResponseEntity.ok(new ApiResponse(true, "Profile fetched successfully!", data));
    }

    // ================== GET ALL TEACHERS ==================
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllTeachers() {
        var teachers = teacherRepository.findAll().stream()
                .map(t -> new TeacherResponse(
                        t.getId(),
                        t.getFullName(),
                        t.getEmail(),
                        t.isEmailConfirmed(),
                        t.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(
                new ApiResponse(true, "Teachers fetched successfully!", teachers));
    }

    // ================== GET TEACHER BY ID ==================

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getTeacherById(@PathVariable Long id) {
        var teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        TeacherResponse dto = new TeacherResponse(
                teacher.getId(),
                teacher.getFullName(),
                teacher.getEmail(),
                teacher.isEmailConfirmed(),
                teacher.getCreatedAt()
        );

        return ResponseEntity.ok(
                new ApiResponse(true, "Teacher fetched successfully!", dto));
    }

    // ================== JOIN REQUESTS (TEACHER) ==================

    @GetMapping("/lessons/{lessonId}/join-requests")
    public ResponseEntity<ApiResponse> getPendingJoinRequests(@PathVariable Long lessonId,
                                                              Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Missing or invalid token!");
        }

        String email = authentication.getName();
        TeacherModel teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        var list = teacherService.getPendingJoinRequests(teacher.getId(), lessonId);
        return ResponseEntity.ok(new ApiResponse(true,
                "Pending join requests fetched successfully!", list));
    }

    @PostMapping("/lessons/{lessonId}/join-requests/approve")
    public ResponseEntity<ApiResponse> approveJoinRequests(@PathVariable Long lessonId,
                                                           @Valid @RequestBody JoinRequestBulkActionRequest request,
                                                           Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Missing or invalid token!");
        }

        String email = authentication.getName();
        TeacherModel teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        var result = teacherService.approveJoinRequests(teacher.getId(), lessonId, request.getStudentIds());
        return ResponseEntity.ok(new ApiResponse(true,
                "Join requests approved successfully.", result));
    }

    @PostMapping("/lessons/{lessonId}/join-requests/reject")
    public ResponseEntity<ApiResponse> rejectJoinRequests(@PathVariable Long lessonId,
                                                          @Valid @RequestBody JoinRequestBulkActionRequest request,
                                                          Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Missing or invalid token!");
        }

        String email = authentication.getName();
        TeacherModel teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        var result = teacherService.rejectJoinRequests(teacher.getId(), lessonId, request.getStudentIds());
        return ResponseEntity.ok(new ApiResponse(true,
                "Join requests rejected successfully.", result));
    }

    // ================== EXAMS (TEACHER) ==================

    @PostMapping("/lessons/{lessonId}/exams")
    public ResponseEntity<ApiResponse> createExam(@PathVariable Long lessonId,
                                                  @Valid @RequestBody CreateExamRequest request,
                                                  Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Missing or invalid token!");
        }

        String email = authentication.getName();
        TeacherModel teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        ExamSummaryDto exam = examService.createExam(teacher.getId(), lessonId, request);
        return ResponseEntity.ok(new ApiResponse(true,
                "Exam created successfully!", exam));
    }

    @GetMapping("/lessons/{lessonId}/exams")
    public ResponseEntity<ApiResponse> getExamsForLesson(@PathVariable Long lessonId,
                                                         Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Missing or invalid token!");
        }

        String email = authentication.getName();
        TeacherModel teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        var exams = examService.getExamsForLessonAsTeacher(teacher.getId(), lessonId);
        return ResponseEntity.ok(new ApiResponse(true,
                "Exams fetched successfully!", exams));
    }

    @PostMapping("/lessons")
    public ResponseEntity<ApiResponse> createLesson(@Valid @RequestBody CreateLessonRequest request,
                                                    Authentication authentication){
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Missing or invalid token!");
        }

        String email = authentication.getName();
        TeacherModel teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Teacher not found!"));

        LessonDto data = lessonService.createLesson(teacher.getId(),request);

        return ResponseEntity.ok(new ApiResponse(true,"Lesson created successfully!",data));

    }

}
