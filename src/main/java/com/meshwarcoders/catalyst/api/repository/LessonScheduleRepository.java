package com.meshwarcoders.catalyst.api.repository;

import com.meshwarcoders.catalyst.api.model.LessonScheduleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonScheduleRepository extends JpaRepository<LessonScheduleModel, Long> {
    List<LessonScheduleModel> findByLessonId(Long lessonId);
}
