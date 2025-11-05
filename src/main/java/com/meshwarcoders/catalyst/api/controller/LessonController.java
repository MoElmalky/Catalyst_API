package com.meshwarcoders.catalyst.api.controller;

import com.meshwarcoders.catalyst.api.dto.ApiResponse;
import com.meshwarcoders.catalyst.api.dto.CreateLessonRequest;
import com.meshwarcoders.catalyst.api.dto.LessonResponseDTO;
import com.meshwarcoders.catalyst.api.model.LessonModel;
import com.meshwarcoders.catalyst.api.model.TeacherModel;
import com.meshwarcoders.catalyst.api.repository.LessonRepository;
import com.meshwarcoders.catalyst.api.repository.TeacherRepository;
import com.meshwarcoders.catalyst.api.security.JwtUtils;
import com.meshwarcoders.catalyst.api.service.LessonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@CrossOrigin(origins = "*")
public class LessonController {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private LessonService lessonService;

    // ================== CREATE LESSON ==================
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createLesson(
            @Valid @RequestBody CreateLessonRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = (String) auth.getPrincipal();

            TeacherModel teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            LessonResponseDTO saved = lessonService.createLesson(teacher, request);

            return ResponseEntity.ok(new ApiResponse(true, "Lesson created successfully!", saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== GET MY LESSONS (للمدرس المسجل) ==================
    @GetMapping("/my-lessons")
    public ResponseEntity<ApiResponse> getMyLessons() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = (String) auth.getPrincipal();

            TeacherModel teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            List<LessonResponseDTO> lessons = lessonService.getLessonsByTeacherId(teacher.getId());

            return ResponseEntity.ok(
                    new ApiResponse(true, "Lessons fetched successfully!", lessons));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== GET LESSONS BY TEACHER ID ==================
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<ApiResponse> getLessonsByTeacherId(@PathVariable Long teacherId) {
        try {
            TeacherModel teacher = teacherRepository.findById(teacherId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found!"));

            List<LessonResponseDTO> lessons = lessonService.getLessonsByTeacherId(teacherId);

            return ResponseEntity.ok(
                    new ApiResponse(true, "Lessons fetched successfully!", lessons));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== GET ALL LESSONS (كل الكلاسات لكل المدرسين) ==================
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllLessons() {
        try {
            List<LessonResponseDTO> lessons = lessonService.getAllLessons();
            return ResponseEntity.ok(
                    new ApiResponse(true, "All lessons fetched successfully!", lessons));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== GET LESSON BY ID ==================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getLessonById(@PathVariable Long id) {
        try {
            LessonResponseDTO lesson = lessonService.getLessonById(id);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Lesson fetched successfully!", lesson));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== UPDATE LESSON ==================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateLesson(
            @PathVariable Long id,
            @Valid @RequestBody CreateLessonRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = (String) auth.getPrincipal();

            TeacherModel teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            LessonModel lesson = lessonRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Lesson not found!"));

            // Verify teacher owns this lesson
            if (!lesson.getTeacher().getId().equals(teacher.getId())) {
                throw new RuntimeException("You are not authorized to update this lesson!");
            }

            LessonResponseDTO updated = lessonService.updateLesson(id, request);

            return ResponseEntity.ok(
                    new ApiResponse(true, "Lesson updated successfully!", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    // ================== DELETE LESSON ==================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteLesson(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = (String) auth.getPrincipal();

            TeacherModel teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            LessonModel lesson = lessonRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Lesson not found!"));

            // Verify teacher owns this lesson
            if (!lesson.getTeacher().getId().equals(teacher.getId())) {
                throw new RuntimeException("You are not authorized to delete this lesson!");
            }

            lessonRepository.delete(lesson);

            return ResponseEntity.ok(
                    new ApiResponse(true, "Lesson deleted successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}