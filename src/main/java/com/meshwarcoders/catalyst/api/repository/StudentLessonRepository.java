package com.meshwarcoders.catalyst.api.repository;

import com.meshwarcoders.catalyst.api.model.LessonModel;
import com.meshwarcoders.catalyst.api.model.StudentLessonModel;
import com.meshwarcoders.catalyst.api.model.StudentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentLessonRepository extends JpaRepository<StudentLessonModel, Long> {
    
    // Check if student is already enrolled in lesson
    boolean existsByStudentAndLesson(StudentModel student, LessonModel lesson);
    
    // Get specific enrollment
    Optional<StudentLessonModel> findByStudentAndLesson(StudentModel student, LessonModel lesson);
    
    // Get all students in a lesson
    List<StudentLessonModel> findByLessonId(Long lessonId);
    
    // Get all lessons for a student
    List<StudentLessonModel> findByStudentId(Long studentId);
}