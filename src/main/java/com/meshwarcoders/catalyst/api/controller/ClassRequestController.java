package com.meshwarcoders.catalyst.api.controller;

import com.meshwarcoders.catalyst.api.dto.*;
import com.meshwarcoders.catalyst.api.repository.StudentRepository;
import com.meshwarcoders.catalyst.api.repository.TeacherRepository;
import com.meshwarcoders.catalyst.api.security.JwtUtils;
import com.meshwarcoders.catalyst.api.service.ClassRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/class-requests")
@CrossOrigin(origins = "*")
public class ClassRequestController {

    @Autowired
    private ClassRequestService classRequestService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    // ================== STUDENT: REQUEST TO JOIN CLASS ==================
    @PostMapping("/join")
    public ResponseEntity<ApiResponse> requestToJoinClass(@Valid @RequestBody JoinClassRequest request) {
        try {
            // ✅ نجيب الـ email من الـ Authentication
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = (String) auth.getPrincipal();
            
            System.out.println("✅ Student email from token: " + email);
            System.out.println("✅ Authorities: " + auth.getAuthorities());

            var student = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Student not found!"));

            ClassRequestResponse response = classRequestService.requestToJoinClass(student.getId(), request);
            return ResponseEntity.ok(new ApiResponse(true, "Request sent successfully!", response));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== STUDENT: GET MY REQUESTS ==================
    @GetMapping("/my-requests")
    public ResponseEntity<ApiResponse> getMyRequests() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = (String) auth.getPrincipal();

            var student = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Student not found!"));

            var requests = classRequestService.getMyRequests(student.getId());
            return ResponseEntity.ok(new ApiResponse(true, "Requests fetched successfully!", requests));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== TEACHER: GET PENDING REQUESTS ==================
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse> getPendingRequests() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = (String) auth.getPrincipal();

            var teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Teacher not found!"));

            var requests = classRequestService.getPendingRequestsForTeacher(teacher.getId());
            return ResponseEntity.ok(new ApiResponse(true, "Pending requests fetched successfully!", requests));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== TEACHER: APPROVE REQUEST ==================
    @PostMapping("/approve/{requestId}")
    public ResponseEntity<ApiResponse> approveRequest(@PathVariable Long requestId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = (String) auth.getPrincipal();

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
    @PostMapping("/reject/{requestId}")
    public ResponseEntity<ApiResponse> rejectRequest(@PathVariable Long requestId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = (String) auth.getPrincipal();

            var teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Teacher not found!"));

            classRequestService.rejectRequest(teacher.getId(), requestId);
            return ResponseEntity.ok(new ApiResponse(true, "Request rejected successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}