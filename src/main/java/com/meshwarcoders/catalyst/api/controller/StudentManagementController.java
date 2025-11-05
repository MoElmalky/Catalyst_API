package com.meshwarcoders.catalyst.api.controller;

import com.meshwarcoders.catalyst.api.dto.ApiResponse;
import com.meshwarcoders.catalyst.api.model.TeacherModel;
import com.meshwarcoders.catalyst.api.repository.TeacherRepository;
import com.meshwarcoders.catalyst.api.service.StudentManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student-management")
@CrossOrigin(origins = "*")
public class StudentManagementController {

    @Autowired
    private StudentManagementService studentManagementService;

    @Autowired
    private TeacherRepository teacherRepository;

    // ================== REMOVE STUDENT FROM ALL MY CLASSES ==================
    @DeleteMapping("/remove-from-all-classes/{studentId}")
    public ResponseEntity<ApiResponse> removeStudentFromAllMyClasses(@PathVariable Long studentId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = (String) auth.getPrincipal();

            TeacherModel teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Teacher not found!"));

            int removedCount = studentManagementService.removeStudentFromAllTeacherClasses(
                    teacher.getId(), studentId);

            String message = String.format(
                    "Student removed successfully from %d class(es)!", removedCount);

            return ResponseEntity.ok(new ApiResponse(true, message, removedCount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== GET STUDENT ENROLLMENT COUNT WITH ME ==================
    @GetMapping("/student-enrollment/{studentId}")
    public ResponseEntity<ApiResponse> getStudentEnrollmentCount(@PathVariable Long studentId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = (String) auth.getPrincipal();

            TeacherModel teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Teacher not found!"));

            int count = studentManagementService.getStudentEnrollmentCountWithTeacher(
                    teacher.getId(), studentId);

            String message = String.format("Student is enrolled in %d of your classes", count);

            return ResponseEntity.ok(new ApiResponse(true, message, count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}