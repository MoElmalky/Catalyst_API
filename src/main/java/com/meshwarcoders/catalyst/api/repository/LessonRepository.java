package com.meshwarcoders.catalyst.api.repository;

import com.meshwarcoders.catalyst.api.model.LessonModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<LessonModel, Long> {
    
    // Get all lessons for a specific teacher
    List<LessonModel> findByTeacherId(Long teacherId);
}