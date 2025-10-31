package com.meshwarcoders.catalyst.api.repository;

import com.meshwarcoders.catalyst.api.model.ClassRequestModel;
import com.meshwarcoders.catalyst.api.model.LessonModel;
import com.meshwarcoders.catalyst.api.model.StudentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRequestRepository extends JpaRepository<ClassRequestModel, Long> {
    
    // Get all requests for a specific lesson
    List<ClassRequestModel> findByLessonId(Long lessonId);
    
    // Get all requests by a specific student
    List<ClassRequestModel> findByStudentId(Long studentId);
    
    // Get pending requests for a specific lesson
    List<ClassRequestModel> findByLessonIdAndStatus(Long lessonId, ClassRequestModel.RequestStatus status);
    
    // Check if student already requested to join this lesson
    Optional<ClassRequestModel> findByStudentAndLesson(StudentModel student, LessonModel lesson);
    
    // Check if student already has a pending request for this lesson
    boolean existsByStudentAndLessonAndStatus(StudentModel student, LessonModel lesson, ClassRequestModel.RequestStatus status);
}