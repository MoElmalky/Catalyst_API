package com.meshwarcoders.catalyst.api.service;

import com.meshwarcoders.catalyst.api.model.LessonModel;
import com.meshwarcoders.catalyst.api.model.StudentLessonModel;
import com.meshwarcoders.catalyst.api.model.StudentModel;
import com.meshwarcoders.catalyst.api.repository.LessonRepository;
import com.meshwarcoders.catalyst.api.repository.StudentLessonRepository;
import com.meshwarcoders.catalyst.api.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentManagementService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private StudentLessonRepository studentLessonRepository;

    // ================== REMOVE STUDENT FROM ALL TEACHER'S CLASSES ==================
    @Transactional
    public int removeStudentFromAllTeacherClasses(Long teacherId, Long studentId) {
        // Get student
        StudentModel student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found!"));

        // Get all teacher's lessons
        List<LessonModel> teacherLessons = lessonRepository.findByTeacherId(teacherId);

        if (teacherLessons.isEmpty()) {
            throw new RuntimeException("Teacher has no lessons!");
        }

        int removedCount = 0;

        // Remove student from each lesson
        for (LessonModel lesson : teacherLessons) {
            var enrollment = studentLessonRepository.findByStudentAndLesson(student, lesson);

            if (enrollment.isPresent()) {
                studentLessonRepository.delete(enrollment.get());
                removedCount++;
            }
        }

        if (removedCount == 0) {
            throw new RuntimeException("Student is not enrolled in any of your classes!");
        }

        return removedCount;
    }

    // ================== GET STUDENT'S ENROLLMENT COUNT WITH TEACHER ==================
    @Transactional(readOnly = true)
    public int getStudentEnrollmentCountWithTeacher(Long teacherId, Long studentId) {
        StudentModel student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found!"));

        List<LessonModel> teacherLessons = lessonRepository.findByTeacherId(teacherId);

        int count = 0;
        for (LessonModel lesson : teacherLessons) {
            if (studentLessonRepository.existsByStudentAndLesson(student, lesson)) {
                count++;
            }
        }

        return count;
    }
}