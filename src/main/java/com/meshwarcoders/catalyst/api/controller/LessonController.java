package com.meshwarcoders.catalyst.api.controller;

import com.meshwarcoders.catalyst.api.model.LessonModel;
import com.meshwarcoders.catalyst.api.model.TeacherModel;
import com.meshwarcoders.catalyst.api.repository.LessonRepository;
import com.meshwarcoders.catalyst.api.repository.TeacherRepository;
import com.meshwarcoders.catalyst.api.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/create")
    public ResponseEntity<?> createLesson(@RequestHeader("Authorization") String authHeader,
                                          @RequestBody LessonModel lesson) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Missing token");
            }

            String token = authHeader.substring(7);
            String email = jwtUtils.getEmailFromToken(token);
            TeacherModel teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            lesson.setTeacher(teacher);
            LessonModel saved = lessonRepository.save(lesson);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my-lessons")
    public ResponseEntity<?> getMyLessons(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Missing token");
            }

            String token = authHeader.substring(7);
            String email = jwtUtils.getEmailFromToken(token);
            TeacherModel teacher = teacherRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            return ResponseEntity.ok(lessonRepository.findByTeacherId(teacher.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
