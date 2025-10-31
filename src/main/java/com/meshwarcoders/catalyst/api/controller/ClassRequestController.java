package com.meshwarcoders.catalyst.api.controller;

import com.meshwarcoders.catalyst.api.dto.*;
import com.meshwarcoders.catalyst.api.model.StudentModel;
import com.meshwarcoders.catalyst.api.repository.StudentRepository;
import com.meshwarcoders.catalyst.api.repository.TeacherRepository;
import com.meshwarcoders.catalyst.api.security.JwtUtils;
import com.meshwarcoders.catalyst.api.service.ClassRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class-requests")
@CrossOrigin(origins = "*")
public class ClassRequestController {

    @Autowired
    private ClassRequestService classRequestService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    // ================== STUDENT: REQUEST TO JOIN CLASS ==================
    @PostMapping("/join")
    public ResponseEntity<ApiResponse> requestToJoinClass(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody JoinClassRequest request) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Missing or invalid token!"));
            }

            String token = authHeader.substring(7);
            String email = jwtUtils.getEmailFromToken(token);

            StudentModel student = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Student not found!"));

            ClassRequestResponse response = classRequestService.requestToJoinClass(student.getId(), request);

            return ResponseEntity.ok(new ApiResponse(true, "Join request sent successfully!", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== STUDENT: GET MY REQUESTS ==================
    @GetMapping("/my-requests")
    public ResponseEntity<ApiResponse> getMyRequests(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Missing or invalid token!"));
            }

            String token = authHeader.substring(7);
            String email = jwtUtils.getEmailFromToken(token);

            StudentModel student = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Student not found!"));

            List<ClassRequestResponse> requests = classRequestService.getMyRequests(student.getId());

            return ResponseEntity.ok(new ApiResponse(true, "Requests fetched successfully!", requests));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== TEACHER: GET PENDING REQUESTS ==================
    @GetMapping("/teacher/pending")
    public ResponseEntity<ApiResponse> getPendingRequests(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Missing or invalid token!"));
            }

            String token = authHeader.substring(7);
            String email = jwtUtils.getEmailFromToken(token);

            var teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Teacher not found!"));

            List<ClassRequestResponse> requests = classRequestService.getPendingRequestsForTeacher(teacher.getId());

            return ResponseEntity.ok(new ApiResponse(true, "Pending requests fetched successfully!", requests));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== TEACHER: APPROVE REQUEST ==================
    @PostMapping("/teacher/approve/{requestId}")
    public ResponseEntity<ApiResponse> approveRequest(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long requestId) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Missing or invalid token!"));
            }

            String token = authHeader.substring(7);
            String email = jwtUtils.getEmailFromToken(token);

            var teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Teacher not found!"));

            classRequestService.approveRequest(teacher.getId(), requestId);

            return ResponseEntity.ok(new ApiResponse(true, "Request approved successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== TEACHER: REJECT REQUEST ==================
    @PostMapping("/teacher/reject/{requestId}")
    public ResponseEntity<ApiResponse> rejectRequest(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long requestId) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Missing or invalid token!"));
            }

            String token = authHeader.substring(7);
            String email = jwtUtils.getEmailFromToken(token);

            var teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Teacher not found!"));

            classRequestService.rejectRequest(teacher.getId(), requestId);

            return ResponseEntity.ok(new ApiResponse(true, "Request rejected successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== TEACHER: REMOVE STUDENT FROM CLASS ==================
    @DeleteMapping("/teacher/remove-student")
    public ResponseEntity<ApiResponse> removeStudentFromClass(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long lessonId,
            @RequestParam Long studentId) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Missing or invalid token!"));
            }

            String token = authHeader.substring(7);
            String email = jwtUtils.getEmailFromToken(token);

            var teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Teacher not found!"));

            classRequestService.removeStudentFromClass(teacher.getId(), lessonId, studentId);

            return ResponseEntity.ok(new ApiResponse(true, "Student removed from class successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== TEACHER: GET STUDENTS IN CLASS ==================
    @GetMapping("/teacher/students/{lessonId}")
    public ResponseEntity<ApiResponse> getStudentsInClass(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long lessonId) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Missing or invalid token!"));
            }

            String token = authHeader.substring(7);
            String email = jwtUtils.getEmailFromToken(token);

            var teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Teacher not found!"));

            List<StudentModel> students = classRequestService.getStudentsInClass(teacher.getId(), lessonId);

            return ResponseEntity.ok(new ApiResponse(true, "Students fetched successfully!", students));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}