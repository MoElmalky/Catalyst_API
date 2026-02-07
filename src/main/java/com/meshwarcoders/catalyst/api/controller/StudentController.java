package com.meshwarcoders.catalyst.api.controller;

import com.meshwarcoders.catalyst.api.dto.response.ApiResponse;
import com.meshwarcoders.catalyst.api.dto.response.JoinStudentDto;
import com.meshwarcoders.catalyst.api.exception.UnauthorizedException;
import com.meshwarcoders.catalyst.api.repository.StudentRepository;
import com.meshwarcoders.catalyst.api.service.ExamService;
import com.meshwarcoders.catalyst.api.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ExamService examService;

    @Autowired
    private StudentRepository studentRepository;

    // ================== JOIN REQUEST (STUDENT) ==================
    @PostMapping("/lesson/{lessonId}/join-request")
    public ResponseEntity<ApiResponse> createJoinRequest(@PathVariable Long lessonId,
                                                         Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Missing or invalid token!");
        }

        String email = authentication.getName();
        JoinStudentDto dto = studentService.createJoinRequest(lessonId, email);

        return ResponseEntity.ok(new ApiResponse(true,
                "Join request created successfully!", dto));
    }

    // ================== EXAMS (STUDENT) ==================
    @GetMapping("/lesson/{lessonId}/exams")
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

    // TODO: Create getAllStudentLessons
    // TODO: Create getStudentLessonWithLessonID
    // TODO: Create getExamWithExamID
}
