package com.meshwarcoders.catalyst.api.repository;

import com.meshwarcoders.catalyst.api.model.ExamModel;
import com.meshwarcoders.catalyst.api.model.LessonModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<ExamModel, Long> {
    List<ExamModel> findByLesson(LessonModel lesson);
}