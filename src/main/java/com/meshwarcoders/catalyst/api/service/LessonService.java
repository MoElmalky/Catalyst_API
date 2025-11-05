package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.dto.CreateLessonRequest;
import com.meshwarcoders.catalyst.api.dto.LessonResponseDTO;
import com.meshwarcoders.catalyst.api.model.LessonModel;
import com.meshwarcoders.catalyst.api.model.LessonScheduleModel;
import com.meshwarcoders.catalyst.api.model.TeacherModel;
import com.meshwarcoders.catalyst.api.repository.LessonRepository;
import com.meshwarcoders.catalyst.api.repository.LessonScheduleRepository;
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

    @Autowired
    private LessonScheduleRepository lessonScheduleRepository;

    // ================== CREATE LESSON WITH SCHEDULES ==================
    @Transactional
    public LessonResponseDTO createLesson(TeacherModel teacher, CreateLessonRequest request) {
        // Create lesson
        LessonModel lesson = new LessonModel();
        lesson.setSubject(request.getSubject());
        lesson.setTeacher(teacher);
        
        lesson = lessonRepository.save(lesson);

        // Create schedules if provided
        if (request.getSchedules() != null && !request.getSchedules().isEmpty()) {
            final LessonModel savedLesson = lesson;
            
            List<LessonScheduleModel> schedules = request.getSchedules().stream()
                    .map(scheduleReq -> {
                        LessonScheduleModel schedule = new LessonScheduleModel();
                        schedule.setLesson(savedLesson);
                        schedule.setStartTime(scheduleReq.getStartTime());
                        schedule.setDuration(scheduleReq.getDuration());
                        return schedule;
                    })
                    .collect(Collectors.toList());

            lessonScheduleRepository.saveAll(schedules);
            
            // Reload lesson with schedules
            lesson = lessonRepository.findById(savedLesson.getId())
                    .orElseThrow(() -> new RuntimeException("Lesson not found after creation!"));
        }

        return new LessonResponseDTO(lesson, 0); // 0 students initially
    }

    // ================== UPDATE LESSON WITH SCHEDULES ==================
    @Transactional
    public LessonResponseDTO updateLesson(Long lessonId, CreateLessonRequest request) {
        LessonModel lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found!"));

        // Update subject
        lesson.setSubject(request.getSubject());
        lesson = lessonRepository.save(lesson);

        // Delete old schedules
        lessonScheduleRepository.deleteByLessonId(lessonId);

        // Create new schedules if provided
        if (request.getSchedules() != null && !request.getSchedules().isEmpty()) {
            final LessonModel savedLesson = lesson;
            
            List<LessonScheduleModel> schedules = request.getSchedules().stream()
                    .map(scheduleReq -> {
                        LessonScheduleModel schedule = new LessonScheduleModel();
                        schedule.setLesson(savedLesson);
                        schedule.setStartTime(scheduleReq.getStartTime());
                        schedule.setDuration(scheduleReq.getDuration());
                        return schedule;
                    })
                    .collect(Collectors.toList());

            lessonScheduleRepository.saveAll(schedules);
            
            // Reload lesson with schedules
            lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new RuntimeException("Lesson not found!"));
        }

        int studentsCount = studentLessonRepository.findByLessonId(lessonId).size();
        return new LessonResponseDTO(lesson, studentsCount);
    }

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