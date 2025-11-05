package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.dto.LessonResponseDTO;
import com.meshwarcoders.catalyst.api.model.LessonModel;
import com.meshwarcoders.catalyst.api.repository.LessonRepository;
import com.meshwarcoders.catalyst.api.repository.StudentLessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private StudentLessonRepository studentLessonRepository;

    // ================== GET ALL LESSONS WITH DETAILS ==================
    @Transactional(readOnly = true)
    public List<LessonResponseDTO> getAllLessons() {
        List<LessonModel> lessons = lessonRepository.findAll();
        
        return lessons.stream()
                .map(lesson -> {
                    int studentsCount = studentLessonRepository.findByLessonId(lesson.getId()).size();
                    return new LessonResponseDTO(lesson, studentsCount);
                })
                .collect(Collectors.toList());
    }

    // ================== GET LESSONS BY TEACHER ID WITH DETAILS ==================
    @Transactional(readOnly = true)
    public List<LessonResponseDTO> getLessonsByTeacherId(Long teacherId) {
        List<LessonModel> lessons = lessonRepository.findByTeacherId(teacherId);
        
        return lessons.stream()
                .map(lesson -> {
                    int studentsCount = studentLessonRepository.findByLessonId(lesson.getId()).size();
                    return new LessonResponseDTO(lesson, studentsCount);
                })
                .collect(Collectors.toList());
    }

    // ================== GET LESSON BY ID WITH DETAILS ==================
    @Transactional(readOnly = true)
    public LessonResponseDTO getLessonById(Long lessonId) {
        LessonModel lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found!"));
        
        int studentsCount = studentLessonRepository.findByLessonId(lessonId).size();
        
        return new LessonResponseDTO(lesson, studentsCount);
    }
}